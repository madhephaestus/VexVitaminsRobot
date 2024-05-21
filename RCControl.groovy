// code here

import org.apache.commons.io.IOUtils;
import  com.neuronrobotics.bowlerstudio.physics.*;
import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.bowlerstudio.threed.*;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.imu.IMUUpdate
import com.neuronrobotics.sdk.addons.kinematics.imu.IMUUpdateListener
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR
import com.neuronrobotics.sdk.common.DeviceManager
import com.neuronrobotics.sdk.util.ThreadUtil
MobileBase base=DeviceManager.getSpecificDevice( "VexVitaminsRobot",{ScriptingEngine.gitScriptRun(	
		"https://github.com/madhephaestus/VexVitaminsRobot.git",
		"VexVitaminsRobot.xml", 
		null )})

TransformNR measuredPose=null ;
IMUUpdateListener listener =new IMUUpdateListener() {

	@Override
	public void onIMUUpdate(IMUUpdate update) {
		measuredPose=new TransformNR(0,0,0,new RotationNR(	-update.getxAcceleration(),
									update.getyAcceleration()-90,	update.getzAcceleration()	))
									.times(new TransformNR(0,0,0,new RotationNR(180,0,0)))
									.times(new TransformNR(0,0,0,new RotationNR(0,90,0)))
	}
	
}
// Add an IMU listener
base.getImu().addvirtualListeners(listener)

try {
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
	TransformNR move = new TransformNR(-5,0,0,new RotationNR(0,5,0))
	double toSeconds=0.03//30 ms for each increment
	for(int i=0;i<40;i++){
		base.DriveArc(move, toSeconds);
		ThreadUtil.wait((int)toSeconds*1000)
		if(measuredPose!=null)
			println "Heading is "+Math.toDegrees(measuredPose.getRotation().getRotationAzimuth())
	}
}catch(Throwable t) {
	t.printStackTrace(System.out)
}
// remove IMU listener at end of script
base.getImu().removevirtualListeners(listener)




