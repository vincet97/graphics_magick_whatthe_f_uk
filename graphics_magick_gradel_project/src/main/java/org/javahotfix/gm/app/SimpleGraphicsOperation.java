package org.javahotfix.gm.app;

import org.im4java.core.*;
import org.im4java.process.Pipe;
import org.im4java.process.ProcessStarter;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;

import javax.imageio.ImageIO;

public class SimpleGraphicsOperation {

	public static void main(String args[]) {

		// Original File on which we are performing operation
		String originalFilePath = "C:\\TestImages\\happybirthday.jpg";

		// Lets Resize Image to 200x200 Resolution for the use as a thumbnail
		String targetFilePath1 = "C:\\TestImages\\happybirthday_thumb.jpg";
		SimpleGraphicsOperation obj1 = new SimpleGraphicsOperation();
		try {
			obj1.resizeImage(originalFilePath, targetFilePath1, 200, 200);
			System.out.println("Success Generating thumbnail of size 200x200.");
		} catch (Exception ex) {
			System.out.println("Unable to Generate the thumbnail. Got Error :" + ex.getMessage());
			ex.printStackTrace();
		}

		// Let Resize Image to 150x150 px and in PNG format
		String targetFilePath2 = "C:\\TestImages\\happybirthday_thumb_PNG.png";
		try {
			FileInputStream fis = new FileInputStream(originalFilePath);
			FileOutputStream fos = new FileOutputStream(targetFilePath2);
			obj1.resizeImage(fis, fos, 150, 150, "png");
			System.out.println("Success Generating thumbnail of size 150x150");

		} catch (Exception ex) {
			System.out.println("Unable to Generate the thumbnail. Got Error :" + ex.getMessage());
			ex.printStackTrace();
		}

		// Let perform multiple operation on the file
		String targetFilePath3 = "C:\\TestImages\\happybirthday_edited.jpg";
		try {
			FileInputStream fis = new FileInputStream(originalFilePath);
			FileOutputStream fos = new FileOutputStream(targetFilePath3);
			obj1.performMultipleOperation(fis, fos);
			System.out.println("Success Perofm Multiple Operation");

		} catch (Exception ex) {
			System.out.println("Unable to Generate the thumbnail. Got Error :" + ex.getMessage());
			ex.printStackTrace();
		}

		// Let append file horizontaly to each other
		String targetFilePath4 = "C:\\TestImages\\combine_images.jpg";
		try {
			FileInputStream fis1 = new FileInputStream("C:\\TestImages\\testimg1.jpg");
			FileInputStream fis2 = new FileInputStream("C:\\TestImages\\testimg2.jpg");
			FileInputStream fis3 = new FileInputStream("C:\\TestImages\\testimg3.jpg");
			FileInputStream fis4 = new FileInputStream("C:\\TestImages\\testimg4.jpg");

			FileOutputStream fos = new FileOutputStream(targetFilePath4);
			obj1.joinMultipleImages(fis1, fis2, fis3, fis4, targetFilePath4);
			System.out.println("Success Perofm Image Joining");

		} catch (Exception ex) {
			System.out.println("Unable to Generate the thumbnail. Got Error :" + ex.getMessage());
			ex.printStackTrace();
		}

		String targetFilePath5 = "C:\\TestImages\\montage_test.jpg";
		try {
			FileInputStream fis1 = new FileInputStream("C:\\TestImages\\testimg1.jpg");

			FileOutputStream fos = new FileOutputStream(targetFilePath4);
			obj1.montageCreator(fis1, "placeholder", targetFilePath5);
			System.out.println("Success Perofm Image Montage");

		} catch (Exception ex) {
			System.out.println("Unable to Generate the montage. Got Error :" + ex.getMessage());
			ex.printStackTrace();
		}

		String targetFilePath6 = "C:\\TestImages\\boldi.jpg";
		try {
			addTextWatermark(targetFilePath6,"C:\\TestImages\\watermark1.jpeg","DT_Qodak_PhotoPlatform!");

		}catch(Exception ex)
		{
			System.out.println("Unable to Generate the montage. Got Error :" + ex.getMessage());
			ex.printStackTrace();
		}


	}

	public SimpleGraphicsOperation() {
		// You Need to set path in below line you dont want to add the graphics magick on PATH Environment Variable of linux or Windows
		// ProcessStarter.setGlobalSearchPath("path graphics magick installation");
	}

	public void resizeImage(
			String originalFile,
			String targetFile,
			int width,
			int height) throws InterruptedException, IOException, IM4JavaException {

		ConvertCmd command = new ConvertCmd(true);
		IMOperation operation = new IMOperation();

		operation.addImage(originalFile);
		operation.resize(width, height);
		operation.addImage(targetFile);

		// Execute the Operation
		command.run(operation);
	}

	public void resizeImage(
			InputStream input,
			OutputStream output,
			int width,
			int height,
			String format) throws IOException, InterruptedException, IM4JavaException {

		ConvertCmd command = new ConvertCmd(true);

		Pipe pipeIn = new Pipe(input, null);
		Pipe pipeOut = new Pipe(null, output);

		command.setInputProvider(pipeIn);
		command.setOutputConsumer(pipeOut);

		IMOperation operation = new IMOperation();
		operation.addImage("-");
		operation.resize(width, height);
		operation.addImage(format + ":-");

		command.run(operation);
	}

	public void performMultipleOperation(
			InputStream input,
			OutputStream output) throws IOException, InterruptedException, IM4JavaException {
		ConvertCmd command = new ConvertCmd(true);
		Pipe pipeIn = new Pipe(input, null);
		Pipe pipeOut = new Pipe(null, output);

		command.setInputProvider(pipeIn);
		command.setOutputConsumer(pipeOut);

		IMOperation operation = new IMOperation();
		operation.addImage("-");
		operation.blur(100.0);
		operation.border(15, 15);
		operation.addImage("-");

		command.run(operation);
	}

	public void joinMultipleImages(
			InputStream input1,
			InputStream input2,
			InputStream input3,
			InputStream input4,
			String output) throws IOException, InterruptedException, IM4JavaException {

		BufferedImage img1 = ImageIO.read(input1);
		BufferedImage img2 = ImageIO.read(input2);
		BufferedImage img3 = ImageIO.read(input3);
		BufferedImage img4 = ImageIO.read(input4);


		ConvertCmd command = new ConvertCmd(true);
		IMOperation operation = new IMOperation();
		operation.addImage();
		operation.addImage();
		operation.addImage();
		operation.addImage();
		operation.appendHorizontally();
		operation.addImage();
		command.run(operation, img1, img2, img3, img4, output);

	}

	public void montageCreator(InputStream input1, String grix_value,
							   String output)
			throws IOException, InterruptedException, IM4JavaException {

		BufferedImage img1 = ImageIO.read(input1);

		String temp = "C:\\TestImages\\temp_montage.jpg";

		ConvertCmd command = new ConvertCmd(true);
		IMOperation operation = new IMOperation();
		MontageCmd montageCmd = new MontageCmd(true);

		operation.addImage();
		operation.addImage();
		operation.addImage();
		operation.addImage();
		operation.tile(2, 2);

		/*
		# Geometry function is extremely fucked up from our pov.
		# Program Flow->
		# First argument gets appended if not null
		# If the first or second arg is not null it appends an "x"
		# Second argument gets appended if not null
		# If the second or third arg is not null it appends a "+"
		# Third argument gets appended if not null
		# If the third or fourth arg is not null it appends a "+"
		# Fourth argument gets appended if not null
		*/

		operation.geometry(null, null, 60, 60);
		operation.addImage();
		montageCmd.run(operation, img1, img1, img1, img1, temp);

		operation = new IMOperation();
		operation.addImage();
		operation.gravity("center");
		//operation.extent(100);
		operation.addImage();
		command.run(operation, temp, output);


	}

	public static void addTextWatermark(String srcImagePath, String destImagePath, String content) throws Exception {
		IMOperation op = new IMOperation();

		op.addImage();
		op.gravity("center");
		op.font("Arial-Bold");
		op.fill("white");
		op.draw("font-size 50 rotate 45 text 0,0 " + content);
		op.quality(100.00);

		op.addImage();




		ConvertCmd command = new ConvertCmd(true);
		command.run(op,srcImagePath,destImagePath);

	}


}






