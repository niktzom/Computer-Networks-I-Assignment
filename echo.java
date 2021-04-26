import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import ithakimodem.Modem;

public class echo {
	public static void echoPackages(Modem modem) throws FileNotFoundException {
		byte[] echo_req = userApplication.echo_request_code.getBytes();

		int j;
		int packages = 0;

		File Output_from_echo = new File("‪Echo.txt");
		File Time_for_echo = new File("‪Time.txt");

		FileOutputStream echo_stream = new FileOutputStream(Output_from_echo);
		FileOutputStream time_stream = new FileOutputStream(Time_for_echo);

		// Έναρξη μέτρησης του χρόνου αποστολής
		long begin = System.currentTimeMillis(); 
		long start, end;
		String echostring = "";
		String response = "";
		
		while ((System.currentTimeMillis() - begin) < 240000) // 4λεπτη αποστολή πακέτων
		{
			String inside_text = "";
			start = System.currentTimeMillis();
			modem.write(echo_req); 
			for (;;) {
				j = modem.read();
				if (j != -1) {
					inside_text += (char) j;
					if (inside_text.indexOf("PSTOP") > -1) {
						packages += 1;
						end = System.currentTimeMillis();
						response += Long.toString(end - start) + "\r\n";
						echostring += inside_text + "\r\n";
						break;
					}
				} else
					break;
			}

		}
		System.out.println(" Packages  received:" + packages + "!");
		System.out.println("Echo pagkages at: " + LocalDateTime.now());
		try {
			echo_stream.write(echostring.getBytes());
			time_stream.write(response.getBytes());
			echo_stream.close();
			time_stream.close();
		} catch (IOException x) {
			System.out.println(x);
		}

	}
}
