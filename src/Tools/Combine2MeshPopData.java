package Tools;

import java.io.File;

import KsymSimulation.Simulation_ver2;

public class Combine2MeshPopData {

	public static void main(String args[]){

		File pop = new File("c:/Users/yabec_000/Desktop/Fujisawa_PT_Mesh5_forSim.csv");
		File obs = new File("C:/Users/yabec_000/Desktop/FujisawaEQProject/Fujisawa_1447_obs.txt"); // define file of observation data
		File meshcodes = new File("C:/Users/yabec_000/Desktop/FujisawaEQProject/FujisawaMeshes_5.csv"); //define meshcode file for area of study
		File result = new File("c:/Users/yabec_000/Desktop/F_2MeshPopResult.csv");
		
		double rmse = Simulation_ver2.getRMSE(pop, obs, meshcodes, result);
		System.out.println(rmse);
		
	}
}
