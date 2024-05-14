// code here

import org.apache.commons.io.IOUtils;
import  com.neuronrobotics.bowlerstudio.physics.*;
import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.bowlerstudio.threed.*;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR
import com.neuronrobotics.sdk.common.DeviceManager
import com.neuronrobotics.sdk.util.ThreadUtil
MobileBase base;
//Check if the device already exists in the device Manager
if(args==null){
	base=DeviceManager.getSpecificDevice( "VexVitaminsRobot",{ScriptingEngine.gitScriptRun(	
		"https://github.com/madhephaestus/VexVitaminsRobot.git",
		"VexVitaminsRobot.xml", 
		null )})
}else
	base=args.get(0)


println "Now we will move just one leg"
DHParameterKinematics arm = base.getAppendages().get(0)
double zLift=25
println "Start from where the arm already is and move it from there with absolute location"
TransformNR current = arm.getCurrentPoseTarget();
current.translateZ(zLift);
arm.setDesiredTaskSpaceTransform(current,  2.0);
ThreadUtil.wait(2000)// wait for the legs to fully arrive