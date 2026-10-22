package dev.cybercore.cyberssh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.cybercore.cyberssh.ui.theme.CyberSSHTheme
import dev.cybercore.cyberssh.R
import androidx.compose.ui.res.painterResource

private val Background = Color(0xFF080B11)
private val SurfaceColor = Color(0xFF111821)
private val SurfaceRaised = Color(0xFF151F29)
private val Cyan = Color(0xFF35DCE6)
private val Green = Color(0xFF83F7A6)
private val Muted = Color(0xFF8795A5)
private val Magenta = Color(0xFFEF58B5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CyberSSHTheme { CyberSSHApp() } }
    }
}

private enum class Screen { DASHBOARD, CONNECT, SESSION }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CyberSSHApp() {
    var screen by rememberSaveable { mutableStateOf(Screen.DASHBOARD) }
    var host by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var port by rememberSaveable { mutableStateOf("22") }
    var notice by rememberSaveable { mutableStateOf("") }

    Scaffold(
        containerColor = Background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .border(1.dp, Cyan.copy(alpha = .7f), RoundedCornerShape(9.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(painterResource(R.drawable.ic_cyberssh_mark), contentDescription = "CyberSSH", modifier = Modifier.size(30.dp))
                        }
                        Spacer(Modifier.width(11.dp))
                        Column {
                            Text("CYBERSSH", fontWeight = FontWeight.Black, letterSpacing = 1.6.sp, fontSize = 15.sp)
                            Text("CYBERCORE / REMOTE OPS", fontFamily = FontFamily.Monospace, color = Muted, fontSize = 8.sp, letterSpacing = 1.sp)
                        }
                    }
                },
                navigationIcon = {
                    if (screen != Screen.DASHBOARD) {
                        IconButton(onClick = { screen = Screen.DASHBOARD; notice = "" }) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = Cyan)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background, titleContentColor = Color.White),
            )
        },
    ) { padding ->
        when (screen) {
            Screen.DASHBOARD -> Dashboard(
                modifier = Modifier.padding(padding),
                onNewConnection = { screen = Screen.CONNECT; notice = "" },
                onOpenAssist = { notice = "AI command assistance will be enabled after the secure session flow is in place." },
                notice = notice,
            )
            Screen.CONNECT -> ConnectScreen(
                modifier = Modifier.padding(padding),
                host = host,
                onHost = { host = it },
                username = username,
                onUsername = { username = it },
                port = port,
                onPort = { port = it.filter(Char::isDigit).take(5) },
                notice = notice,
                onConnect = {
                    notice = when {
                        host.isBlank() -> "Enter a hostname or IP address."
                        username.isBlank() -> "Enter the SSH username."
                        port.toIntOrNull() !in 1..65535 -> "Port must be between 1 and 65535."
                        else -> "Connection transport is being wired. Your fields are held in memory only."
                    }
                },
            )
            Screen.SESSION -> SessionScreen(Modifier.padding(padding), host)
        }
    }
}

@Composable
private fun Dashboard(modifier: Modifier, onNewConnection: () -> Unit, onOpenAssist: () -> Unit, notice: String) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(7.dp).background(Green, RoundedCornerShape(50)))
                    Eyebrow("LOCAL CONTROL PLANE / READY")
                }
                Text("Your remote\nworkspace.", fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 34.sp, color = Color.White)
                Text("A secure SSH cockpit with a full terminal and an AI command co-pilot you stay in control of.", color = Muted, fontSize = 14.sp, lineHeight = 20.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    ActionButton(label = "NEW CONNECTION", onClick = onNewConnection, modifier = Modifier.weight(1f), icon = { Icon(Icons.Outlined.Add, null, modifier = Modifier.size(16.dp)) })
                    IconButton(
                        onClick = onOpenAssist,
                        modifier = Modifier.size(46.dp).border(1.dp, Magenta.copy(alpha = .45f), RoundedCornerShape(12.dp)),
                    ) { Text("AI", color = Magenta, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) }
                }
            }
        }

        if (notice.isNotBlank()) NoticeCard(notice)

        SectionTitle("SAVED CONNECTIONS", "0 NODES")
        EmptyState(onNewConnection)

        SectionTitle("COMMAND CO-PILOT", "OPTIONAL")
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceRaised), shape = RoundedCornerShape(14.dp)) {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Outlined.Lock, null, tint = Green, modifier = Modifier.size(20.dp))
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("You review. You run.", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("AI suggestions will be shown as plain commands with explanations and risk labels. Nothing executes without your tap.", color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
                }
            }
        }
        Text("SSH host keys are verified before authentication. Passwords and private keys are never sent to the AI provider.", color = Muted, fontSize = 10.sp, lineHeight = 15.sp)
    }
}

@Composable
private fun ConnectScreen(
    modifier: Modifier,
    host: String,
    onHost: (String) -> Unit,
    username: String,
    onUsername: (String) -> Unit,
    port: String,
    onPort: (String) -> Unit,
    notice: String,
    onConnect: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Eyebrow("01 / SECURE ENDPOINT")
        Text("Connect to a node", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Text("Enter the remote host. Credentials will be requested only when the SSH transport is ready.", color = Muted, fontSize = 13.sp, lineHeight = 19.sp)
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceColor), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CyberField("HOST / IP ADDRESS", host, onHost, placeholder = "server.example.net", keyboardType = KeyboardType.Uri)
                CyberField("USERNAME", username, onUsername, placeholder = "operator")
                CyberField("SSH PORT", port, onPort, placeholder = "22", keyboardType = KeyboardType.Number)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.Lock, null, tint = Green, modifier = Modifier.size(17.dp))
                    Text("Host fingerprint confirmation will be required on first contact.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
                }
                ActionButton(label = "CONTINUE TO AUTHENTICATION", onClick = onConnect, modifier = Modifier.fillMaxWidth())
            }
        }
        if (notice.isNotBlank()) NoticeCard(notice)
        Text("Passwords are session-only. Key files are encrypted at rest using Android Keystore-backed protection.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun SessionScreen(modifier: Modifier, host: String) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Eyebrow("SSH / CONNECTED SESSION")
        Text(host, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        TerminalPreview()
        Text("Terminal transport adapter is not connected yet.", color = Muted, fontSize = 12.sp)
    }
}

@Composable
private fun TerminalPreview() {
    Column(
        Modifier.fillMaxWidth().height(320.dp).background(Color(0xFF05080C), RoundedCornerShape(12.dp)).border(1.dp, Color(0xFF263642), RoundedCornerShape(12.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Text("REMOTE TERMINAL / PREVIEW", color = Cyan, fontFamily = FontFamily.Monospace, fontSize = 10.sp, letterSpacing = 1.sp)
        Spacer(Modifier.height(4.dp))
        Text("No remote process attached", color = Muted, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
        Text("Connect to a host to open an interactive shell.", color = Muted, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        Spacer(Modifier.weight(1f))
        Text("$", color = Green, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
    }
}

@Composable
private fun EmptyState(onNewConnection: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor.copy(alpha = .72f)),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Icon(Icons.Outlined.Terminal, null, tint = Cyan, modifier = Modifier.size(26.dp))
            Text("No saved nodes", color = Color.White, fontWeight = FontWeight.SemiBold)
            Text("Add a host to begin a secure SSH session.", color = Muted, fontSize = 12.sp)
            Button(onClick = onNewConnection, colors = ButtonDefaults.buttonColors(containerColor = SurfaceRaised, contentColor = Cyan)) {
                Text("ADD FIRST NODE", fontFamily = FontFamily.Monospace, fontSize = 11.sp, letterSpacing = .5.sp)
            }
        }
    }
}

@Composable
private fun CyberField(label: String, value: String, onValue: (String) -> Unit, placeholder: String, keyboardType: KeyboardType = KeyboardType.Text) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Eyebrow(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValue,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(placeholder, color = Muted.copy(alpha = .7f), fontSize = 13.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Cyan,
                unfocusedBorderColor = Color(0xFF2B3945),
                cursorColor = Cyan,
                focusedContainerColor = Color(0xFF0A0F15),
                unfocusedContainerColor = Color(0xFF0A0F15),
            ),
        )
    }
}

@Composable
private fun ActionButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, icon: (@Composable () -> Unit)? = null) {
    Button(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(11.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Background),
    ) {
        if (icon != null) icon()
        if (icon != null) Spacer(Modifier.width(5.dp))
        Text(label, fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = .4.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun Eyebrow(text: String) {
    Text(text, color = Cyan, fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.05.sp)
}

@Composable
private fun SectionTitle(title: String, tag: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = .6.sp)
        Spacer(Modifier.weight(1f))
        Text(tag, color = Muted, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
    }
}

@Composable
private fun NoticeCard(text: String) {
    Surface(color = Color(0xFF17232B), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
        Text(text, Modifier.padding(13.dp), color = Cyan, fontSize = 12.sp, lineHeight = 17.sp)
    }
}
