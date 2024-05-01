import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.IDriveEngine
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR

import Jama.Matrix;

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
		newPose = newPose.inverse()
		//println "Move "+newPose.toSimpleString()
		try {
			HashMap<DHParameterKinematics,MobileBase> wheels = getDrivable(base)
			for(DHParameterKinematics LimbWithWheel:wheels.keySet()){
				MobileBase wheelSource=wheels.get(LimbWithWheel);
				TransformNR global= base.getFiducialToGlobalTransform();
				if(global==null){
					global=new TransformNR()
					base.setGlobalToFiducialTransform(global)
				}
				global=global.times(newPose);// new global pose
				TransformNR tipCheck = new TransformNR(0,0,1)
				int wheelIndex = LimbWithWheel.getNumberOfLinks()-1
				//println "Wheel "+LimbWithWheel.getScriptingName()+" index "+wheelIndex
				TransformNR wheelStarting;
				
				if(wheelIndex==0) {
					wheelStarting=wheelSource.forwardOffset(LimbWithWheel.getRobotToFiducialTransform())
				}else {
					wheelStarting=wheelSource.forwardOffset(LimbWithWheel.getLinkTip(wheelIndex-1))
				}
				TransformNR zvect = wheelStarting.times(tipCheck)
				TransformNR orentation = new TransformNR(
					-wheelStarting.getX()+zvect.getX(),
					-wheelStarting.getY()+zvect.getY(),
					-wheelStarting.getZ()+zvect.getZ()
					)
				double orentAngle =Math.atan2(orentation.getY(),orentation.getX())
				println "Z pointing in "+Math.toDegrees(orentAngle)
				
			}
		}catch(Throwable t) {
			t.printStackTrace()
		}
	}
	
}