package slayer;

import util.DataAccessor;

import javax.xml.bind.JAXBException;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by jpcmarques on 03-10-2016.
 */
public abstract class SlayerMasterAssignmentsGenerator {
    public static void generate(DropData data){
        SlayerMasterAssignments assignments = data.getSlayerMasterAssignments();
        List<SlayerMasterAssignments.SlayerMaster> slayerMasters = assignments.getSlayerMaster();
        for(Monster monster : data.getMonsterList().getMonster()){
            for(SlayerMaster master : monster.getMasterList().getMaster()){
                boolean slayerMasterExists = false;
                SlayerMasterAssignments.SlayerMaster foundMaster = null;
                for (SlayerMasterAssignments.SlayerMaster slayerMaster : slayerMasters){
                    if(slayerMaster.getName() == master){
                        foundMaster = slayerMaster;
                        slayerMasterExists = true;
                        break;
                    }
                }
                if(!slayerMasterExists){
                    SlayerMasterAssignments.SlayerMaster slayerMaster = new SlayerMasterAssignments.SlayerMaster();
                    slayerMaster.setName(master);
                    slayerMaster.setTotalWeight(BigInteger.valueOf(0));
                    foundMaster = slayerMaster;
                    slayerMasters.add(slayerMaster);
                }
                SlayerMasterAssignments.SlayerMaster.Assignment assignment = new SlayerMasterAssignments.SlayerMaster.Assignment();
                System.out.println("ARCHETYPE: \n" +monster.getSlayercat());
                assignment.setArchetype(monster.getSlayercat());
                assignment.setWeight(BigInteger.valueOf(0));
                foundMaster.getAssignment().add(assignment);
            }
            System.out.println("MonsterID: \n" +monster.getMonsterID());
        }
    }
}
