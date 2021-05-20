import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;

import ithakimodem.Modem;

public class images extends echo {
	
	public static void imageWithoutError(Modem modem) throws FileNotFoundException {
		byte[] imgreq = userApplication.image_free_request_code.getBytes();
		modem.write(imgreq);

	
		
		FileOutputStream img_free_stream =null;
		File image_free = new File("image_free.jpeg");
		
		try {
			img_free_stream = new FileOutputStream(image_free);
			
		}catch (IOException x) {
			System.out.println("Exception! Cannot initialize the output stream. The output file remain as null " );
		}

		int current_pixel;
		int last_pixel = 0;
		boolean image_flag = false;

		ArrayList<Byte> img_free = new ArrayList<Byte>();
		//Check for the starting delimeter of our image
		do {
			current_pixel = modem.read();
			if (current_pixel == 255) {
				last_pixel = current_pixel;
				current_pixel = modem.read();
				if (current_pixel == 216) {
					image_flag = true;
					System.out.println("Image receiving has been started!");
					
					img_free.add((byte) last_pixel);
					img_free.add((byte) current_pixel);
				}
			}
		} while (!image_flag);
		
		if (!image_flag) {
			System.err.println("Exit! Start of Image delimiter is missing.");
			System.exit(0);
		}
		for (;;) {
			current_pixel = modem.read();

			if (current_pixel == -1)
				break;
			//Check for the last delimiter of our image
			if (last_pixel == 255 && current_pixel == 217) {
				System.out.println("Image received!");
				img_free.add((byte) current_pixel);
				break;
			}
			img_free.add((byte) current_pixel);
			last_pixel = current_pixel;
		}
		System.out.println("Image without error at " + LocalDateTime.now());
		//arraylist to .jpeg form
		try {
			img_free_stream.write(convertBytes(img_free));
			img_free_stream.close();
		}
		catch (Exception x) {
			System.out.println(x);
		}
		
	}
	
	
	
	public static void imageWithError(Modem modem) throws FileNotFoundException {
		byte[] imgreq = userApplication.image_error_request_code.getBytes();
		modem.write(imgreq);

		FileOutputStream img_error_stream = null;
		File image_with_error = new File("‪image_with_error.jpeg");
		
		try {
			img_error_stream = new FileOutputStream(image_with_error);
			
		}catch (IOException x) {
			System.out.println("Exception! Cannot initialize the output stream. The output file remain as null " );
		}
		int pixel_cur;
		int last_pixel = 0;
		boolean image_e_flag = false;

		ArrayList<Byte> img_with_error = new ArrayList<Byte>();
		//Check for the starting delimeter of our image
		do {
			pixel_cur = modem.read();
			if (pixel_cur == 255) {
				last_pixel = pixel_cur;
				pixel_cur = modem.read();
				if (pixel_cur == 216) {
					image_e_flag = true;
					System.out.println("Image Receiving (the error one) has been started!");

					img_with_error.add((byte) last_pixel);
					img_with_error.add((byte) pixel_cur);
				}
			}
		} while (!image_e_flag);

		if (!image_e_flag) {
			System.err.println("Exit! Start of Image delimiter is missing.");
			System.exit(0);
		}
		for (;;) {
			pixel_cur = modem.read();

			if (pixel_cur == -1)
				break;
			//Check for the last delimiter of our image
			if (last_pixel == 255 && pixel_cur == 217) {
				System.out.println("Image with error received!");
				img_with_error.add((byte) pixel_cur);
				break;
			}
			img_with_error.add((byte) pixel_cur);
			last_pixel = pixel_cur;
		}
		System.out.println("Image with error at " + LocalDateTime.now());
		// arraylist to .jpeg form.
		try {
			img_error_stream.write(convertBytes(img_with_error));
			img_error_stream.close();
		} catch (Exception x) {
			System.out.println(x);
		}

	}
	
	
	public static byte[] convertBytes(ArrayList<Byte> bytes) {
		byte[] ret = new byte[bytes.size()];
		Iterator<Byte> iterator = bytes.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next().byteValue();
		}
		return ret;
	}
}
