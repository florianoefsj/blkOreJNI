import java.lang.String;
import java.util.Arrays;
import java.io.*;
import java.nio.file.Files;

public class blkOreJNI {
	static {
	         System.loadLibrary("oreblkc");
        }
     
	private native void blkOreC(String args[], int op);
            
	private byte[] keybytes;
	private byte[] ciphertext;
	private int nbits;
	private int block_len;
	private int res;

	public static void main(String[] args) throws FileNotFoundException, IOException{
		
		int op=0;

		blkOreJNI blkorejni = new blkOreJNI();
		if (args.length > 2)
		switch (args[0]){
			case "enc":{
				if (args.length < 4) {
					System.out.println("Uso: enc secret_key n outputfile | cmp secret_key ctxt1 ctxt2 | test secret_key n1 n2");
					break;}
				System.out.print("Encrypt >> ");
				op = 1;
				blkorejni.blkOreC(args, op);
				byte[] ciphertext1 = blkorejni.ciphertext;
				String path = args[3];
				FileOutputStream fos = new FileOutputStream(path);
				fos.write(ciphertext1);
				fos.close();
				System.out.println(args[3]);
				break;
			}
			case "cmp":{
				op = 2;

				System.out.print("Compare: "+ args[1]);
				String file1 = args[1];
				byte[] ciphertext1 = Files.readAllBytes(new File(file1).toPath());
				String ctxt1_str = new String("");

				for (byte b: ciphertext1){
					ctxt1_str = ctxt1_str + String.format("%02X", b);
				}

				String file2 = args[2];
				byte[] ciphertext2 = Files.readAllBytes(new File(file2).toPath());
				String ctxt2_str = new String("");

				for (byte b: ciphertext2){
					ctxt2_str = ctxt2_str + String.format("%02X", b);
				}

				String [] new_args = {"cmp", ctxt1_str, ctxt2_str};
				blkorejni.blkOreC(new_args, op);
				int res = blkorejni.res;
				System.out.println(" "+res+" "+args[2]);
				break;
			}
			case "test":{
				op = 1;
				String [] new_args = {"enc", args[1], args[2]};
				blkorejni.blkOreC(new_args, op);
				byte[] ciphertext1 = blkorejni.ciphertext;

				new_args[2] = args[3];
				blkorejni.blkOreC(new_args, op);
				byte[] ciphertext2 = blkorejni.ciphertext;
				
				String ctxt1_str = new String("");
				for (byte b: ciphertext1){
					ctxt1_str = ctxt1_str + String.format("%02X", b);
				}

				String ctxt2_str = new String("");
				for (byte b: ciphertext2){
					ctxt2_str = ctxt2_str + String.format("%02X", b);
				}

				op = 2;
				new_args[0] = "cmp";
				new_args[1] = ctxt1_str;
				new_args[2] = ctxt2_str;
				blkorejni.blkOreC(new_args, op);
				int res = blkorejni.res;
				System.out.println("cmp: "+res);

				break;}
			default:{
				System.out.println("Uso: enc secret_key n outputfile | cmp secret_key ctxt1 ctxt2 | test secret_key n1 n2");
			}
		}
		else
			System.out.println("Uso: enc secret_key n outputfile | cmp secret_key ctxt1 ctxt2 | test secret_key n1 n2");

	
	}
}
