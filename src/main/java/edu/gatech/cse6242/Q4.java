package edu.gatech.cse6242;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.IOException;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class Q4 {

   /*Mapper1*/

  public static class EdgeWeightMapper
       extends Mapper<Object, Text, Text, IntWritable>{

    Text textKey1 = new Text();
    Text textKey2 = new Text();
    final static IntWritable one = new IntWritable(1);
    final static IntWritable mone = new IntWritable(-1);

    @Override
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {


      String line = value.toString();
      String[] field = line.split("\t");
      if (field.length == 2) {
	String s = String.valueOf(field[0]);
	String t = String.valueOf(field[1]);
	
	textKey1.set(s);
	context.write(textKey1, one);
	textKey2.set(t);
	context.write(textKey2, mone);

      }
    }
   }

  /*Mapper2*/

  public static class EdgeWeightMapper2
       extends Mapper<LongWritable, Text, Text, IntWritable>{

    Text textKey1 = new Text();
    final static IntWritable one = new IntWritable(1);

    @Override
    public void map(LongWritable key, Text value, Context context
                    ) throws IOException, InterruptedException {


      String line = value.toString();
      String[] field = line.split("\t");
      
      String t = String.valueOf(field[1]);
	
      textKey1.set(t);
      context.write(textKey1, one);

    }
   }

  /*Reducer1*/

  public static class EdgeWeightReducer
       extends Reducer<Text, IntWritable, Text, IntWritable> {
    Text textValue = new Text();

    private IntWritable result = new IntWritable(); 

    //@Override
    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
    int sum = 0;
    
    for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result);
    }	
 }

  /*Reducer2*/

  public static class EdgeWeightReducer2
       extends Reducer<Text, IntWritable, Text, IntWritable> {
    Text textValue = new Text();

    private IntWritable result = new IntWritable(); 

    //@Override
    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
    int sum = 0;
    
    for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      //result.set(1);
      context.write(key, result);
    }	
 }



  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    
    Job job = new Job(conf, "Q4");

    job.setJarByClass(Q4.class);
    job.setMapperClass(EdgeWeightMapper.class);
    job.setCombinerClass(EdgeWeightReducer.class);
    job.setReducerClass(EdgeWeightReducer.class);

    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path("./temp"));

    job.waitForCompletion(true);
    
    //Job2

    Job job2 = Job.getInstance(conf, "Q2b");
    job2.setJarByClass(Q4.class);

    job2.setInputFormatClass(TextInputFormat.class);

    job2.setMapperClass(EdgeWeightMapper2.class);
    //job.setCombinerClass(EdgeWeightReducer.class);
    job2.setReducerClass(EdgeWeightReducer2.class);

    job2.setMapOutputKeyClass(Text.class);
    job2.setMapOutputValueClass(IntWritable.class);

    job2.setOutputKeyClass(Text.class);
    job2.setOutputValueClass(IntWritable.class);

    
    //job.setOutputFormatClass(TextOutputFormat.class);



    FileInputFormat.addInputPath(job2, new Path("./temp"));
    FileOutputFormat.setOutputPath(job2, new Path(args[1]));


    System.exit(job2.waitForCompletion(true) ? 0 : 1);
  }
}
