package project5.project5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

public class InputPaser {

	public static class TokenizerMapper extends
			Mapper<Object, Text, Text, DoubleWritable> {

		private Text word = new Text();
		private final static DoubleWritable one = new DoubleWritable(1.0);
		
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			// System.out.println(value.toString());
			word.set(value.toString().substring(
					value.toString().indexOf("<title>") + "<title>".length(),
					value.toString().indexOf("</title>")));
			context.write(word, one);
			Pattern pattern = Pattern.compile("\\[\\[[^\\]]*\\]\\]");
			Matcher matcher = pattern.matcher(value.toString());
			List<String> links = new ArrayList<String>();
			
			while (matcher.find()) {
				String tem = matcher.group().replace("[", "").replaceAll("]", "");
				links.add(tem);
				word.set(tem);
				context.write(word, one);
				PageRank.values.put(word.toString(), null);
			}
			PageRank.values.put(word.toString(), links);
		}
	}

	public static class IntSumReducer extends
			Reducer<Text, DoubleWritable, Text, DoubleWritable> {

		public void reduce(Text key, Iterable<DoubleWritable> values,
				Context context) throws IOException, InterruptedException {
			//System.out.println(key.toString());
			for (DoubleWritable value : values) {
				//System.out.println(key.toString()+value.toString());
				context.write(key, value);
			}

		}
	}

	public static void Pase() {
		Configuration conf = new Configuration();
		Job job;
		try {
			job = Job.getInstance(conf, "Parse");
			job.setJarByClass(InputPaser.class);
			job.setMapperClass(TokenizerMapper.class);
			job.setCombinerClass(IntSumReducer.class);
			job.setReducerClass(IntSumReducer.class);
			job.setOutputFormatClass(SequenceFileOutputFormat.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(DoubleWritable.class);
			FileInputFormat.addInputPath(job, new Path("wiki-micro.txt"));
			FileOutputFormat.setOutputPath(job, new Path("Mapreduce0"));
			try {
				job.waitForCompletion(true);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
