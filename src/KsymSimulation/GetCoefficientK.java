package KsymSimulation;

public class GetCoefficientK {

	public static double getK(Integer age, Double dis){
		Double k;
		if(age<4){
			if(dis >= 10){
				k = 0d;
			}
			else{
				k = (2-0.1*dis);
			}
		}
		else if(age==4 || age==5 || age==8 || age==9){
			if(dis >= 20){
				k = 0d;
			}
			else if(dis <= 10){
				k = 1d;
			}
			else {
				k = 2-0.1*dis;
			}
		}
		else{
			if(dis >= 20){
				k = 0d;
			}
			else if(dis <= 12){
				k = 1d;
			}
			else {
				k = (2.5-0.125*dis);
			}
		}
		return k;
	}
	
}
