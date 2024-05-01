import com.neuronrobotics.bowlerstudio.creature.ICadGenerator
import com.neuronrobotics.bowlerstudio.creature.MobileBaseCadManager
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Cube
import javafx.scene.transform.Affine

return new ICadGenerator(){

	@Override
	public ArrayList<CSG> generateCad(DHParameterKinematics d, int i) {
		MobileBaseCadManager manager  = MobileBaseCadManager.get(d.getLinkConfiguration(i));
		TransformNR offset = d.getDHStep(i).inverse();
		
		ArrayList<CSG> back =[]
		back.add(new Cube(1).toCSG())
		for(CSG c:back)
			c.setManipulator(d.getLinkObjectManipulator(i))
		Affine lastLinkAffine = i==0? d.getRootListener() :d.getListener(i-1);
		Affine manipulator = d.getListener(i);
		
		if(manager!=null) {
			back.addAll(manager.getOriginVitaminsDisplay(
				d.getAbstractLink(i),
				manipulator,offset));
			back.addAll(manager.getDefaultVitaminsDisplay(
				d.getAbstractLink(i),
				manipulator));
			back.addAll(manager.getPreviousLinkVitaminsDisplay(
				d.getAbstractLink(i),
				lastLinkAffine));
		}else{
			println "No manager found for "+d.getScriptingName()+" "+i
		}
		for(CSG c:back) {
			c.getStorage().set("no-physics",true)
		}
		if(manager!=null) {
			back.addAll(manager.getOriginVitamins(
				d.getAbstractLink(i),
				manipulator,offset));
			back.addAll(manager.getDefaultVitamins(
				d.getAbstractLink(i),
				manipulator));
			back.addAll(manager.getPreviousLinkVitamins(
				d.getAbstractLink(i),
				lastLinkAffine));
		}
		for(CSG c:back) {
			if( c!=null)
				c.setManufacturing({return null})
		}
		return back;
	}

	@Override
	public ArrayList<CSG> generateBody(MobileBase b) {
		MobileBaseCadManager manager  = MobileBaseCadManager.get(b);
		ArrayList<CSG> back =[]
		back.addAll(manager.getVitaminsDisplay(b,b.getRootListener()));
		for(CSG c:back) {
			c.getStorage().set("no-physics",true)
		}
		back.addAll(manager.getVitamins(b,b.getRootListener()));
		for(CSG c:back) {
			c.setManufacturing({return null})
		}
		for(DHParameterKinematics kin:b.getAllDHChains()) {
			CSG limbRoot =new Cube(1).toCSG()
			limbRoot.setManipulator(kin.getRootListener())
			back.add(limbRoot)

		}
		return back;
	}
	
	
}
