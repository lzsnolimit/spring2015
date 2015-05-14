package com.bigdata;

/*Zhongshan Lu
 * Assignment1
 */
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

public class WordCount {

	public static class Map extends MapReduceBase implements
			Mapper<LongWritable, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(LongWritable key, Text value,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			String line = value.toString();
			String regEx = "[^a-z\u0020]";
			line = line.toLowerCase().replaceAll(regEx, "");
			word.set(line);
			output.collect(word, one);
		}
	}

	public static class Reduce extends MapReduceBase implements
			Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterator<IntWritable> values,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			if (key.toString().length() == 0) {
				return;
			}
			String words[] = key.toString().split(" ");
			Arrays.sort(words);

			int sum = 0;
			while (values.hasNext()) {
				sum += values.next().get();
			}
			String temWord = "";
			output.collect(key, new IntWritable(sum));
			Vector<String> strs = new Vector<String>();
			Vector<Integer> numbers = new Vector<Integer>();
			int i = 0;
			int start = i;
			for (; i < (words.length - 1); i++) {
				if (!words[start].equals(words[i + 1])) {
					if (words[i].length() != 0) {
						strs.add(words[i]);
						numbers.add(i + 1 - start);
					}
					start = i + 1;
				}
			}
			strs.add(words[i]);
			numbers.add(i + 1 - start);

			for (int j = 0; j < strs.size(); j++) {
				if (words[i].equals(" ")) {
					System.out.println("here");
				}
			}
			String currentRow = "";
			String currentSentence = key.toString();
			for (int j = 0; j < strs.size(); j++) {
				temWord = "";
				if (strs.get(j).length() != 0) {
					currentRow = strs.get(j);

					for (int k = 0; k < strs.size(); k++) {

						if (k != j && strs.get(k).length() != 0) {
							temWord += strs.get(k) + " occurs "
									+ numbers.get(k);
						}
					}
					Hbase.addData(currentRow, currentSentence, temWord);
				}

			}
		}
	}

	public static void main(String[] args) throws Exception {
		Hbase.setStrings("assignment3", "wordcount");
		Hbase.createTable();

		JobConf conf = new JobConf(WordCount.class);
		conf.setJobName("wordcount");

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);

		conf.setMapperClass(Map.class);
		conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);

		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		FileInputFormat.setInputPaths(conf, new Path("input.txt"));
		FileOutputFormat.setOutputPath(conf, new Path("output"));

		JobClient.runJob(conf);
		SwingContiner  swingContainerDemo = new SwingContiner();
		Hbase.getAllData();
	}
}