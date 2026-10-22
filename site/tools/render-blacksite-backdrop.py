import bpy
import math
from mathutils import Vector

# Render a static, cinematic Blacksite hardware environment. No character/mascot
# elements: the scene is a physical operations console and server equipment.
bpy.ops.object.select_all(action="SELECT")
bpy.ops.object.delete(use_global=False)
for datablocks in (bpy.data.materials, bpy.data.curves, bpy.data.meshes, bpy.data.cameras, bpy.data.lights):
    pass

def material(name, color, metallic=0.0, roughness=0.5, emission=None, strength=0.0):
    m = bpy.data.materials.new(name)
    m.diffuse_color = (*color, 1)
    m.use_nodes = True
    p = m.node_tree.nodes.get("Principled BSDF")
    p.inputs["Base Color"].default_value = (*color, 1)
    p.inputs["Metallic"].default_value = metallic
    p.inputs["Roughness"].default_value = roughness
    if metallic > .35:
        nodes=m.node_tree.nodes; links=m.node_tree.links
        noise=nodes.new("ShaderNodeTexNoise"); noise.inputs["Scale"].default_value=145
        noise.inputs["Detail"].default_value=3.2; noise.inputs["Roughness"].default_value=.78
        tex=nodes.new("ShaderNodeTexCoord")
        links.new(tex.outputs["Generated"],noise.inputs["Vector"])
        bump=nodes.new("ShaderNodeBump"); bump.inputs["Strength"].default_value=.12; bump.inputs["Distance"].default_value=.018
        links.new(noise.outputs["Fac"],bump.inputs["Height"]); links.new(bump.outputs["Normal"],p.inputs["Normal"])
        ramp=nodes.new("ShaderNodeMapRange"); ramp.inputs["From Min"].default_value=0; ramp.inputs["From Max"].default_value=1
        ramp.inputs["To Min"].default_value=max(.12,roughness-.12); ramp.inputs["To Max"].default_value=min(.78,roughness+.2)
        links.new(noise.outputs["Fac"],ramp.inputs["Value"]); links.new(ramp.outputs["Result"],p.inputs["Roughness"])
    if emission:
        p.inputs["Emission Color"].default_value = (*emission, 1)
        p.inputs["Emission Strength"].default_value = strength
    return m

armor = material("Graphite titanium", (0.085, 0.105, 0.125), .82, .29)
edge = material("Machined titanium edges", (.24, .29, .32), .88, .24)
black = material("Ceramic black", (.012, .018, .025), .28, .24)
screen = material("Inactive glass", (.006, .018, .025), .38, .12)
cyan = material("Cyan status lamps", (.01, .18, .22), .2, .22, (.005, .62, .82), 3.0)
green = material("Green indicators", (.06, .2, .08), .1, .25, (.06, .8, .12), 2.3)
amber = material("Amber indicators", (.3, .09, .015), .1, .25, (1.0, .15, .015), 1.6)
rubber = material("Cable rubber", (.008, .011, .014), .0, .65)
wall = material("Blacksite background", (.006, .009, .015), .15, .52)

def cube(name, loc, scale, mat, bevel=.05, rot=(0,0,0)):
    bpy.ops.mesh.primitive_cube_add(size=1, location=loc, rotation=rot)
    o=bpy.context.object; o.name=name; o.dimensions=scale
    bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
    o.data.materials.append(mat)
    if bevel:
        mod=o.modifiers.new("CNC softened edges", "BEVEL"); mod.width=bevel; mod.segments=3
        o.modifiers.new("Machined normals", "WEIGHTED_NORMAL")
    return o

def cyl(name, loc, radius, depth, mat, axis="Z", vertices=32):
    rot=(0,0,0) if axis=="Z" else ((math.pi/2,0,0) if axis=="Y" else (0,math.pi/2,0))
    bpy.ops.mesh.primitive_cylinder_add(vertices=vertices, radius=radius, depth=depth, location=loc, rotation=rot)
    o=bpy.context.object; o.name=name; o.data.materials.append(mat)
    mod=o.modifiers.new("Turned edge", "BEVEL"); mod.width=.025; mod.segments=2
    o.modifiers.new("Machined normals", "WEIGHTED_NORMAL")
    return o

def cable(name, points, radius, mat):
    c=bpy.data.curves.new(name,"CURVE"); c.dimensions="3D"; c.resolution_u=16
    c.bevel_depth=radius; c.bevel_resolution=3
    s=c.splines.new("BEZIER"); s.bezier_points.add(len(points)-1)
    for b,p in zip(s.bezier_points,points):
        b.co=p; b.handle_left_type="AUTO"; b.handle_right_type="AUTO"
    o=bpy.data.objects.new(name,c); bpy.context.collection.objects.link(o); o.data.materials.append(mat)
    return o

# Deep, layered equipment bay; the left half stays intentionally quiet for copy.
cube("Rear acoustic wall", (0,0,-3.2), (18,10,.5), wall, .1)
for x in (2.7,4.5,6.3,8.1):
    cube("Rack upright", (x,0,-2.5), (.12,8,.16), edge, .025)
    cube("Rack shadow channel", (x+.12,0,-2.39), (.08,7.8,.04), black, .01)

# Server cabinet faces recede in depth at the right, with real rack proportions.
for x in (3.6,5.4,7.2):
    cube("42U cabinet outer shell", (x,0,-1.35), (1.62,7.55,2.85), armor, .12, rot=(0,.04,0))
    cube("Recessed perforated door", (x,0,.11), (1.4,7.15,.12), black, .055)
    cube("Door inner panel", (x,0,.19), (1.28,6.95,.035), armor, .025)
    # Repeated vent slots and modular compute sleds.
    for row in range(10):
        y=-3.05+row*.66
        cube("Hot-swap compute sled", (x,y,.25), (1.08,.47,.10), black, .025)
        cube("Sled alloy pull", (x-.42,y,.31), (.18,.20,.035), edge, .018)
        for j in range(5):
            cube("Vent groove", (x-.05+j*.07,y-.02,.311), (.018,.21,.008), edge, .004)
        cyl("Drive activity light", (x+.43,y,.32), .026,.025, cyan if (row+x)%3 else green)
    for y in (-3.35,3.35):
        for z in (-1.95,2.1): cyl("Cabinet fastener", (x,y,z), .035,.03,edge,axis="Y",vertices=16)

# Foreground rugged control desk, deliberately offset right to preserve heading area.
cube("Console pedestal", (4.35,-2.75,1.35), (7.7,1.2,2.05), black,.18,rot=(.07,0,-.06))
cube("Titanium console lip", (4.35,-2.05,2.18), (7.95,.16,.24), edge,.055,rot=(.07,0,-.06))
cube("Control deck", (4.35,-1.99,2.31), (7.65,1.05,.12), armor,.055,rot=(.07,0,-.06))
# Main glass display, with dark screen and subtle status telemetry.
cube("Recessed glass status display", (5.75,-1.83,2.39), (2.30,.69,.035), screen,.025,rot=(.07,0,-.06))
for x in (4.82,5.08,5.34,5.60,5.86,6.12,6.38,6.64):
    cube("Display signal trace", (x,-1.79,2.414), (.12,.012,.006), cyan if x in (5.08,6.38) else edge,.003)
for x in (1.55,1.92,2.29,2.66,3.03,3.40):
    cube("Backlit tactile key", (x,-1.84,2.42), (.25,.23,.09), black,.035)
    cube("Key legend light", (x,-1.83,2.47), (.075,.018,.008), cyan if x in (1.55,3.03) else edge,.004)
for x,mat in ((7.17,cyan),(7.48,green),(7.79,amber)):
    cyl("Panel rotary control", (x,-1.84,2.5),.115,.16,edge)
    cyl("Control center", (x,-1.84,2.59),.035,.018,mat)
    cube("Safety switch guard", (7.98,-1.84,2.48),(.24,.31,.15),edge,.04)
    cyl("Guarded control", (7.98,-1.84,2.58),.055,.05,amber)

# Routed fiber and power looms connect console to equipment, adding physical depth.
for n in range(5):
    x=3.05+n*.16
    cable("Braided power loom", [(x,-3.8,1.1),(x+.2,-3.7,.1),(x+.15,-2.7,-.25),(3.5,-1.2,-.3)], .035 if n%2 else .05, rubber)
for x in (3.6,5.4,7.2):
    for y in (-3.1,-2.8,-2.5):
        cyl("Optical trunk connector",(x,y,-.03),.07,.16,edge,axis="Y")

# Small practical status lights only; no floating neon or graphic motifs.
for x in (3.2,4.8,6.4,8.0):
    bpy.ops.object.light_add(type="AREA", location=(x,2.2,4.2))
    l=bpy.context.object; l.name="Cool overhead practical"; l.data.energy=220; l.data.shape="RECTANGLE"; l.data.size=2.2; l.data.size_y=.35
    l.rotation_euler=(math.radians(12),0,math.radians(180))

def area(name, loc, color, energy, size, target):
    bpy.ops.object.light_add(type="AREA", location=loc)
    o=bpy.context.object; o.name=name; o.data.energy=energy; o.data.color=color; o.data.shape="DISK"; o.data.size=size
    o.rotation_euler=(Vector(target)-o.location).to_track_quat("-Z","Y").to_euler()

area("Soft cyan equipment spill", (6,1,6),(.18,.55,.72),480,6,(4,0,0))
area("Cool titanium key", (9,-2,5),(.45,.62,.76),560,5,(5,-1,0))
area("Low neutral fill", (0,-1,4),(.20,.27,.34),160,7,(3,0,0))
area("Warm instrument bounce", (7,-5,3),(.70,.30,.14),65,3,(5,-2,0))

world=bpy.data.worlds.new("Blacksite darkness") if not bpy.data.worlds else bpy.data.worlds[0]
bpy.context.scene.world=world; world.use_nodes=True
world.node_tree.nodes["Background"].inputs["Color"].default_value=(.008,.012,.02,1)
world.node_tree.nodes["Background"].inputs["Strength"].default_value=.18

bpy.ops.object.camera_add(location=(0,0,19))
cam=bpy.context.object; cam.name="Wide Blacksite establishing camera"; cam.data.type="ORTHO"; cam.data.ortho_scale=16.0
cam.rotation_euler=(0,0,0)
scene=bpy.context.scene; scene.camera=cam
scene.render.engine="CYCLES"
scene.cycles.samples=24
scene.cycles.use_denoising=True
scene.render.resolution_x=1920; scene.render.resolution_y=1080; scene.render.resolution_percentage=100
scene.render.image_settings.file_format="PNG"; scene.render.image_settings.color_mode="RGB"; scene.render.image_settings.compression=10
scene.view_settings.view_transform="AgX"
scene.view_settings.look="AgX - High Contrast"
scene.view_settings.exposure=-.85
scene.camera.data.lens=48
scene.render.filepath="/home/raven/Devspace/Android/cybercore-ssh/site/assets/blacksite-operations-bay.png"
scene.render.film_transparent=False
bpy.ops.render.render(write_still=True)
