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
		//newPose = newPose.inverse()
		//println "Move "+newPose.toSimpleString()
		try {
			HashMap<DHParameterKinematics,MobileBase> wheels = getDrivable(base)
			for(DHParameterKinematics LimbWithWheel:wheels.keySet()){
				MobileBase wheelSource=wheels.get(LimbWithWheel);
//				TransformNR global= base.getFiducialToGlobalTransform();
//				if(global==null){
//					global=new TransformNR()
//					base.setGlobalToFiducialTransform(global)
//				}
//				global=global.times(newPose);// new global pose
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
				double orentAngle =Math.toDegrees( Math.atan2(orentation.getY(),orentation.getX()))
				
				TransformNR newWheelPose = newPose.times(wheelStarting)
				double newX = newWheelPose.getX()-wheelStarting.getX()
				double newY = newWheelPose.getY()-wheelStarting.getY()
				double newDelta = Math.sqrt(Math.pow(newX, 2)+Math.pow(newY, 2))
				double deltaOrentation = Math.toDegrees(Math.atan2(newY,newX))
				if(deltaOrentation>90||deltaOrentation<-90)
					newDelta=-newDelta
				if(orentAngle>0) {
					//println "Left"
				}else {
					//println "Right"
					newDelta=-newDelta
				}
				
				double theta = 180.0 * newDelta/(LimbWithWheel.getDH_R(wheelIndex)*Math.PI)
				
				double[] currVal=LimbWithWheel.getCurrentJointSpaceVector();
				try{
					currVal[wheelIndex]+= theta;
					double best = LimbWithWheel.getBestTime(currVal);
					if(best>seconds)
						seconds=best;
					LimbWithWheel.setDesiredJointAxisValue(wheelIndex,currVal[wheelIndex],seconds);
				}catch(Exception e){
						e.printStackTrace(System.out)
				}
			}
		}catch(Throwable t) {
			t.printStackTrace()
		}
	}
	
}