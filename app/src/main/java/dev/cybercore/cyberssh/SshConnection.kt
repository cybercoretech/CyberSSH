package dev.cybercore.cyberssh

import android.content.Context
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.ClientChannel
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.keyverifier.ServerKeyVerifier
import java.io.Closeable
import java.io.OutputStream
import java.security.MessageDigest
import java.security.PublicKey
import java.util.Base64
import java.util.concurrent.TimeUnit

/** SSH transport. Host keys are pinned only after an explicit first-contact approval. */
internal class SshConnectionManager(context: Context) {
    private val knownHosts = context.getSharedPreferences("ssh_known_hosts", Context.MODE_PRIVATE)

    fun knownFingerprint(host: String, port: Int): String? = knownHosts.getString(hostKey(host, port), null)

    fun probeFingerprint(host: String, port: Int, username: String): String {
        var observed: String? = null
        val client = SshClient.setUpDefaultClient()
        client.serverKeyVerifier = ServerKeyVerifier { _, _, key ->
            observed = fingerprint(key)
            true // Probe only: no identity is provided and no authentication is attempted.
        }
        client.start()
        try {
            client.connect(username, host, port).verify(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS).session.use { }
        } finally {
            client.stop()
        }
        return observed ?: error("The server did not present a host key.")
    }

    fun trustFingerprint(host: String, port: Int, fingerprint: String) {
        knownHosts.edit().putString(hostKey(host, port), fingerprint).apply()
    }

    fun connect(
        host: String,
        port: Int,
        username: String,
        password: String,
        expectedFingerprint: String,
        onOutput: (String) -> Unit,
    ): RemoteShell {
        val client = SshClient.setUpDefaultClient()
        client.serverKeyVerifier = ServerKeyVerifier { _, _, key -> fingerprint(key) == expectedFingerprint }
        client.start()
        var session: ClientSession? = null
        var channel: ClientChannel? = null
        try {
            session = client.connect(username, host, port).verify(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS).session
            session.addPasswordIdentity(password)
            session.auth().verify(AUTH_TIMEOUT_SECONDS, TimeUnit.SECONDS)

            val shell = RemoteShell(client, session, onOutput)
            val shellChannel = session.createShellChannel()
            channel = shellChannel
            shellChannel.setPtyType("xterm-256color")
            shellChannel.setPtyColumns(100)
            shellChannel.setPtyLines(32)
            shellChannel.setAgentForwarding(false)
            channel.setIn(shell.stdin)
            channel.setOut(shell.output)
            channel.setErr(shell.output)
            channel.open().verify(AUTH_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            shell.attach(channel)
            return shell
        } catch (failure: Throwable) {
            runCatching { channel?.close(false) }
            runCatching { session?.close(false) }
            client.stop()
            throw failure
        }
    }

    private fun hostKey(host: String, port: Int) = "$host:$port"

    private fun fingerprint(key: PublicKey): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(key.encoded)
        return "SHA256:" + Base64.getEncoder().withoutPadding().encodeToString(digest)
    }

    companion object {
        private const val CONNECT_TIMEOUT_SECONDS = 20L
        private const val AUTH_TIMEOUT_SECONDS = 25L
    }
}

internal class RemoteShell(
    private val client: SshClient,
    private val session: ClientSession,
    onOutput: (String) -> Unit,
) : Closeable {
    private val inputPipe = java.io.PipedInputStream(64 * 1024)
    private val stdinPipe = java.io.PipedOutputStream(inputPipe)
    val stdin: java.io.InputStream = inputPipe
    val output: OutputStream = object : OutputStream() {
        override fun write(value: Int) = publish(byteArrayOf(value.toByte()))
        override fun write(bytes: ByteArray, offset: Int, length: Int) = publish(bytes.copyOfRange(offset, offset + length))
        private fun publish(bytes: ByteArray) {
            if (bytes.isNotEmpty()) onOutput(bytes.toString(Charsets.UTF_8))
        }
    }
    @Volatile private var channel: ClientChannel? = null

    fun attach(channel: ClientChannel) { this.channel = channel }

    fun send(text: String) {
        synchronized(stdinPipe) {
            stdinPipe.write(text.toByteArray(Charsets.UTF_8))
            stdinPipe.flush()
        }
    }

    override fun close() {
        runCatching { stdinPipe.close() }
        runCatching { channel?.close(false) }
        runCatching { session.close(false) }
        client.stop()
    }
}
