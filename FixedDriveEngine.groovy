import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.IDriveEngine
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR

// code here

return new com.neuronrobotics.sdk.addons.kinematics.IDriveEngine (){

	public HashMap<DHParameterKinematics,MobileBase> getDrivable(MobileBase source) {
		HashMap<DHParameterKinematics,MobileBase> copy = new HashMap<>();
		for(def k:source.getDrivable())
			copy.put(k,source);
		for(DHParameterKinematics k:source.getAllDHChains()) {
			for(int i=0;i<k.getNumberOfLinks();i++) {
				if(k.getFollowerMobileBase(i)!=null)
					copy.putAll(getDrivable(k.getFollowerMobileBase(i)))
			}
		}
		return copy;
	}
	@Override
	public void DriveArc(MobileBase base, TransformNR newPose, double seconds) {
		HashMap<DHParameterKinematics,MobileBase> wheels = getDrivable(base)
		
	}
	
}