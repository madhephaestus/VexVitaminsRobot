// code here

import org.apache.commons.io.IOUtils;
import  com.neuronrobotics.bowlerstudio.physics.*;
import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.bowlerstudio.threed.*;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR
import com.neuronrobotics.sdk.common.DeviceManager
import com.neuronrobotics.sdk.util.ThreadUtil
MobileBase base=DeviceManager.getSpecificDevice( "VexVitaminsRobot",{ScriptingEngine.gitScriptRun(	
		"https://github.com/madhephaestus/VexVitaminsRobot.git",
		"VexVitaminsRobot.xml", 
		null )})

println "Now we will move just one arm"
DHParameterKinematics arm = base.getAppendages().get(0)

double zLift=25
println "Start from where the arm already is and move it from there with absolute location"
TransformNR current = arm.getCurrentPoseTarget();

TransformNR absolute = new TransformNR(400,0,5)

current.translateZ(zLift);
arm.setDesiredTaskSpaceTransform(absolute,  2.0);
ThreadUtil.wait(2000)// wait for the arm to fully arrive

// move the ring in positive Y by 50 mm
TransformNR move = new TransformNR(-5,0,0,new RotationNR())
double toSeconds=0.03//30 ms for each increment
for(int i=0;i<40;i++){
	base.DriveArc(move, toSeconds);
	ThreadUtil.wait((int)toSeconds*1000)
}