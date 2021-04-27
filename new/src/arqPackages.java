import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

import ithakimodem.Modem;

public class arqPackages extends gpsCoordinates {
	public static void ARQerror(Modem modem) throws FileNotFoundException {
		byte[] ackreq = userApplication.ACK_request_code.getBytes();

		File arq_output = new File("‪Arq.txt");
		FileOutputStream arq_stream = new FileOutputStream(arq_output);

		File Time_for_arq = new File("Time_arq.txt");
		FileOutputStream time_arq_stream = new FileOutputStream(Time_for_arq);

		//For 4 minutes, check every package -until the "PSTOP" substring- if the XOR of the encrypted bytes is equal to FCS.
		//Then for every package, measure the retries which are needed to achieve the equallity. 
		//If the equality was achieved with the first attempt, give ACK request else give NACK request.
		long start, end;
		int k, ACK_pack = 0, arq_pack = 0, retry = 0;
		int[] NACK_pack_retries = new int[8];
		String text = "", all_text = "", response = "";
		long new_begin = System.currentTimeMillis();
		
		
		while (System.currentTimeMillis() - new_begin < 240000) {
			modem.write(ackreq);
			start = System.currentTimeMillis();
			text = "";
			for (;;) {
				k = modem.read();
				if (k != -1) {
					text += (char) k;
					if (text.indexOf("PSTOP") > -1) {
						if (check_for_arq(text)) {
							ACK_pack += 1;
							end = System.currentTimeMillis();
							response += Long.toString(end - start) + "\r\n";
						} else {
							retry = 0;
							while (!check_for_arq(text)) {
								text = "";
								retry++;
								modem.write(userApplication.NACK_request_code.getBytes());
								for (;;) {
									k = modem.read();
									if (k != -1) {
										text += (char) k;
										if (text.indexOf("PSTOP") > -1) {
											break;
										}
									} else
										break;

								}

							}
							end = System.currentTimeMillis();
							response += Long.toString(end - start) + "\r\n";
							switch (retry)

							{
							case 1:
								NACK_pack_retries[0] += 1;
								break;
							case 2:
								NACK_pack_retries[1] += 1;
								break;
							case 3:
								NACK_pack_retries[2] += 1;
								break;
							case 4:
								NACK_pack_retries[3] += 1;
								break;
							case 5:
								NACK_pack_retries[4] += 1;
								break;
							case 6:
								NACK_pack_retries[5] += 1;
								break;
							case 7:
								NACK_pack_retries[6] += 1;
								break;
							case 8:
								NACK_pack_retries[7] += 1;
								break;
							default:
								break;
							}

						}
						arq_pack += 1;
						break;
					}
				} else
					break;

			}
			if (k == -1 && text == "")
				break;

		}
		int NACK_pack = 0;
		for (int i : NACK_pack_retries) {
			NACK_pack += i;
		}
		System.out.println("ARQ packages " + arq_pack);
		System.out.println("ACK packages " + ACK_pack);
		System.out.println("NACK packages " + NACK_pack);
		System.out.println("Retries ->  times");
		for (int i = 0; i < NACK_pack_retries.length; i++) {
			int index = i + 1;
			System.out.println(  index + "           " + NACK_pack_retries[i]);
		}
		System.out.println("ARQ Error at " + LocalDateTime.now());
		try {
			arq_stream.write(all_text.getBytes());
			arq_stream.close();
			time_arq_stream.write(response.getBytes());
			time_arq_stream.close();
		} catch (Exception x) {
			System.out.println(x);
		}

	}

			//We check the string inside each package if XOR is equal to FCS using the boolean method
	public static boolean check_for_arq(String text) {
		
		String sequence;
		int FCS;

		sequence = text.substring(text.indexOf("<") + 1, text.indexOf(">"));
		int prevC;
		int currC;
		prevC = (int) sequence.charAt(0);
		for (int i = 1; i < sequence.length(); i++) {
			currC = (int) sequence.charAt(i);
			prevC = (prevC ^ currC);
		}
		FCS = Integer.parseInt(text.substring(text.indexOf(">") + 2, text.indexOf("PSTOP") - 1));
		return (FCS == prevC);

	}
}
