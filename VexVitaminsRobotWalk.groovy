import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.sdk.addons.kinematics.IDriveEngine
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR

// code here

return new com.neuronrobotics.sdk.addons.kinematics.IDriveEngine (){
	IDriveEngine walking = ScriptingEngine.gitScriptRun("https://github.com/OperationSmallKat/Marcos.git", "MarcosWalk.groovy")
	IDriveEngine driving = ScriptingEngine.gitScriptRun("https://github.com/NeuronRobotics/NASACurisoity.git", "DriveEngine.groovy")
	
	@Override
	public void DriveArc(MobileBase base, TransformNR newPose, double seconds) {
		IDriveEngine engine=driving;
		if(base.getLegs().size()>0) {
			engine=walking;
		}else if(base.getDrivable().size()>0) {
			engine=driving;
		}else if(base.getSteerable().size()>0) {
			engine=driving;
		}
		engine.DriveArc( base,  newPose,  seconds);
	}
	
}