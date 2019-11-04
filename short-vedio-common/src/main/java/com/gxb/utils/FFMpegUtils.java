package com.gxb.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FFMpegUtils {

    private String ffmpegEXE;

    public FFMpegUtils(String ffmpegEXE) {
        super();
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath,
            String mp3InputPath,double seconds,String videoOutputPath) throws IOException {
        //ffmpeg -i input.mp4 output.avi
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);

        command.add("-i");
        command.add(videoInputPath);

        command.add("-i");
        command.add(mp3InputPath);

        command.add("-t");
        command.add(String.valueOf(seconds));

        command.add("-y");
        command.add(videoOutputPath);

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while((line = br.readLine()) != null) {

        }
        if(inputStreamReader!=null) {
            inputStreamReader.close();
        }
        if(br!=null) {
            br.close();
        }
        if(errorStream!=null) {
            errorStream.close();
        }

    }

    public void getCover(String videoInputPath,String coverOutputPath) throws IOException {
        //ffmpeg -i input.mp4 output.avi
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);

        //指定截取第一秒
        command.add("-ss");
        command.add("00:00:01");

        command.add("-y");
        command.add("-i");
        command.add(videoInputPath);

        command.add("-vframes");
        command.add("1");

        command.add(coverOutputPath);




        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while((line = br.readLine()) != null) {

        }
        if(inputStreamReader!=null) {
            inputStreamReader.close();
        }
        if(br!=null) {
            br.close();
        }
        if(errorStream!=null) {
            errorStream.close();
        }

    }

    public static void main(String[] args) {
        try {
            FFMpegUtils ffMpegUtils = new FFMpegUtils("E:\\mySoftware\\ffmpeg\\ffmpeg-20180723-d134b8d-win64-static\\bin\\ffmpeg.exe");
            ffMpegUtils.getCover("E:\\mySoftware\\ffmpeg\\ffmpeg-20180723-d134b8d-win64-static\\bin\\shadowsocks.avi","E:\\mySoftware\\ffmpeg\\ffmpeg-20180723-d134b8d-win64-static\\bin\\new.jpg");
            // ffMpegUtils.convertor("E:\\mySoftware\\ffmpeg\\ffmpeg-20180723-d134b8d-win64-static\\bin\\wx49b42ac51b544582.o6zAJs0Z_ZnF0YosH5GVz6kouGck.wvFmaKEEDqR094df0192c9a9a001a44befbf7984c4f6.mp4",
            //         "E:\\mySoftware\\ffmpeg\\ffmpeg-20180723-d134b8d-win64-static\\bin\\小林未郁 - BRE@TH／／LESS.mp3",
            //         6,"E:\\mySoftware\\ffmpeg\\ffmpeg-20180723-d134b8d-win64-static\\bin\\newVideo.mp4"
            //         );
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
