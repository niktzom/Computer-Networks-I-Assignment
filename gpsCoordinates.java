import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import ithakimodem.Modem;

public class gpsCoordinates extends images {
	public static void gpsCoordinatesR(Modem modem, String req) throws IOException {
		String GpsPoints = "R=1018099\r";// Session 2
			//String GpsPoints = "R=1010099\r";// Session 1
			req = req + GpsPoints;
			byte[] req_gps =req.getBytes();
			modem.write(req_gps);

			int j;
			double num;
			String gps_txt = "";
			String Amplitude;
			String temp_string;
			String Length;
			String[] per_line;
			
			
			FileOutputStream  Gps_req_stream=null;
			File Output_from_gps_req = new File("Gps_req.txt");

			try {
				Gps_req_stream = new FileOutputStream(Output_from_gps_req);
				
			}catch (IOException x) {
				System.out.println("Exception! Cannot initialize the output stream. The output file remain as null " );
			}
			String GpsPointsR = "";
			for (;;) {
				j = modem.read();
				if (j == -1)
					break;
				gps_txt += (char) j;
			}

			try {
				Gps_req_stream.write(gps_txt.getBytes());
				Gps_req_stream.close();
			} catch (IOException x) {
				System.out.println(x);
			}
			// Διαχωρισμός του string με τις πληροφορίες του gps για κάθε γραμμή.
			per_line = gps_txt.split("\r\n");
			int count = 0;
			double time = 0, curr_time = 0;
			//Για κάθε γραμμή που περιέχει το "$GPGGA", βρίσκουμε το κομμάτι που αναφέρεται στον χρόνο.
			if (per_line[1].indexOf("$GPGGA") > -1) {
				temp_string = per_line[1].substring(per_line[1].indexOf("$GPGGA") + 7, per_line[1].indexOf("$GPGGA") + 13);
				time = Double.parseDouble(temp_string);
			}
			
			int index = 1;
			for (int requests = 0; requests < 4; requests++) {
				curr_time = time;
				//Για κάθε γραμμή βρίσκουμε το κομμάτι που αναφέρεται στο πλάτος(Amplitude).
				temp_string = per_line[index].substring(per_line[index].indexOf("A") + 13,
						per_line[index].indexOf("N") - 1);
				num = Double.parseDouble(temp_string);
				Amplitude = convertCoordinates(num);
				// αντίστοιχα το length.
				temp_string = per_line[index].substring(per_line[index].indexOf("N") + 2, per_line[index].indexOf("E") - 1);
				num = Double.parseDouble(temp_string);
				Length = convertCoordinates(num);
				GpsPointsR += "T=" + Length + Amplitude;
				// Διαφορά 15 δευτέρων για κάθε διαδοχικό σημείο.
				while (count < 99 && time - curr_time < 15) {
					index = 1 + count;
					if (per_line[index].indexOf("$GPGGA") > -1) {
						temp_string = per_line[index].substring(per_line[index].indexOf("$GPGGA") + 7,
								per_line[index].indexOf("$GPGGA") + 13);
						time = Double.parseDouble(temp_string);
					}
					count++;
				}
			}
			GpsPointsR += "\r";
			String new_req = userApplication.gps_request_code + GpsPointsR;
			byte[] gps_new_req = new_req.getBytes();
			modem.write(gps_new_req);

			int last_j = 0;
			boolean image_gps_flag = false;
			ArrayList<Byte> img_with_gps = new ArrayList<Byte>();

			FileOutputStream  img_gps_stream=null;

			File image_with_gps = new File("‪image_with_gps.jpeg");
			
			try {
				img_gps_stream = new FileOutputStream(image_with_gps);
			}catch (IOException x) {
				System.out.println("Exception! Cannot initialize the output stream. The output file remain as null " );
			}

			do {
				j = modem.read();
				if (j == -1)
					break;

				if (j == 255) {
					last_j = j;
					j = modem.read();
					if (j == 216) {
						image_gps_flag = true;

						img_with_gps.add((byte) last_j);
						img_with_gps.add((byte) j);
					}
				}
			} while (!image_gps_flag);

			for (;;) {
				j = modem.read();
				if (j == -1)
					break;
				img_with_gps.add((byte) j);
			}
			System.out.println("GPS with R at " + LocalDateTime.now());
			try {
				img_gps_stream.write(convertBytes(img_with_gps));
				img_gps_stream.close();

			} catch (Exception x) {
				System.out.println(x);
			}

		}
	

	
	public static String convertCoordinates(double num) {
		// Δημιουργία του num
		String final_string;
		int int_part;
		double fr_part;
		int sec;
		int_part = (int) num; 
		fr_part = num - int_part; 
		sec = (int) Math.round(fr_part * 60); 
		final_string = String.valueOf(int_part) + String.valueOf(sec);

		return final_string;
	}
}
