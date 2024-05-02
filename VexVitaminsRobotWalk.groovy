import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.sdk.addons.kinematics.IDriveEngine
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR

return new com.neuronrobotics.sdk.addons.kinematics.IDriveEngine (){
	IDriveEngine walking = ScriptingEngine.gitScriptRun("https://github.com/OperationSmallKat/Marcos.git", "MarcosWalk.groovy")
	IDriveEngine driving = ScriptingEngine.gitScriptRun("https://github.com/NeuronRobotics/NASACurisoity.git", "DriveEngine.groovy")
	IDriveEngine fixed = ScriptingEngine.gitScriptRun("https://github.com/madhephaestus/VexVitaminsRobot.git", "FixedDriveEngine.groovy")
	
	@Override
	public void DriveArc(MobileBase base, TransformNR newPose, double seconds) {
		IDriveEngine engine=fixed;
		if(base.getLegs().size()>0) {
			engine=walking;
		}else if(base.getDrivable().size()>0) {
			engine=fixed;
		}else if(base.getSteerable().size()>0) {
			engine=driving;
		}
		engine.DriveArc( base,  newPose,  seconds);
	}
	
}