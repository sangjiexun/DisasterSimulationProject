package Tools;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class LinkChooser {
	public static void main(String[] args) throws Exception {

		File infile = new File("C:/Users/yabec_000/Desktop/roaddata.csv");
		File nodefile = new File("C:/Users/yabec_000/Desktop/Tokyo_network/network/drm_node.csv");
		String outfile= "c:/Users/yabec_000/Desktop/Tokyo_road.csv";

		ArrayList<String> nodes = new ArrayList<String>();

		BufferedReader br1 = new BufferedReader (new FileReader(nodefile));
		String linkline = null;
		while ( (linkline= br1.readLine()) != null ) {
			String linkids[] = linkline.split(",");
			String linkID = linkids[0];
			nodes.add(linkID);
		}
		br1.close();
		System.out.println("made node map!");

		try {
			BufferedReader br = new BufferedReader (new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = br.readLine();
			int i = 0;
			int j = 0;
			while( (line= br.readLine()) != null ) {
				String tokens[] = line.split(";");
				String id = tokens[0];
				String node1 = tokens[2];
				String node2 = tokens[3];
				String length = tokens[4];
				String geom = tokens[8];
				
				if((nodes.contains(node1))&&(nodes.contains(node2))){
					bw.write(id + "," + node1 + "," + node2 + "," + "2" + "," + "0" + "," + length + "," + "2" + "," + geom);
					bw.newLine();
					j = j + 1;
				}
				System.out.println(i + "," + j);
				i = i+1;
				}
			br.close();
			bw.close();
		}
		catch(FileNotFoundException e) {
			System.out.println("File not found: " + outfile);
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}
}
