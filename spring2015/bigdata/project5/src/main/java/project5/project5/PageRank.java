package project5.project5;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

public class PageRank {
	public static Map<String, List<String>> values = new TreeMap<String, List<String>>();
	public static Configuration conf = new Configuration();
	public final static int limit = 10;

	/**
	 * @author Zhongshan Lu
	 */

	public static class pageRandMapper extends
			Mapper<Text, DoubleWritable, Text, DoubleWritable> {

		private DoubleWritable num = new DoubleWritable(1);
		private final static DoubleWritable zero = new DoubleWritable(0.0);
		private Text word = new Text();

		public void map(Text key, DoubleWritable value, Context context)
				throws IOException, InterruptedException {

			// StringTokenizer itr = new StringTokenizer(value.toString()
			// .replaceAll("(\\[\\[)|(\\]\\])", " "));
			// System.out.println(key.toString()+value.toString());
			List<String> links = PageRank.values.get(key.toString());

			if (links != null && links.size() != 0) {
				int size = links.size();
				Double tem = 0.0;
				// System.out.println(String.valueOf(value.get())+String.valueOf(size));
				// tem=value.get()/size;
				num.set(value.get() / size);
				for (int i = 0; i < links.size(); i++) {
					word.set(links.get(i));
					context.write(word, num);
				}
			} else {
				context.write(key, zero);
			}

			// System.out.println(key.toString()+value.toString());
			// context.write(value, one);

		}
	}

	public static class pageRankReducer extends
			Reducer<Text, DoubleWritable, Text, DoubleWritable> {
		private DoubleWritable result = new DoubleWritable();
		private final static double alpha = 0.15;

		public void reduce(Text key, Iterable<DoubleWritable> values,
				Context context) throws IOException, InterruptedException {
			double sum = 0;
			// System.out.println(key.toString());
			for (DoubleWritable val : values) {
				// System.out.println(val.get());
				sum += val.get();
			}
			result.set((1 - alpha) * sum + alpha / PageRank.values.size());
			//System.out.println(key.toString() + result.toString());
			context.write(key, result);
		}
	}

	public static class sortMapper extends
			Mapper<Text, DoubleWritable, DoubleWritable,Text> {
		public void map(Text key, DoubleWritable value, Context context)
				throws IOException, InterruptedException {
			context.write(value, key);
		}
	}

	public static class sortReducer extends
			Reducer<DoubleWritable,Text, DoubleWritable,Text> {
		public void reduce(DoubleWritable key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			for (Text val : values) {
				// System.out.println(val.get());
				System.out.println(String.valueOf(key.get()*10000)+val.toString());
				context.write(key, val);
			}
		}
	}

	public static void SubPageRank(int initial) {
		Job job;
		try {
			job = Job.getInstance(conf, "PageRank");
			job.setInputFormatClass(SequenceFileInputFormat.class);
			job.setOutputFormatClass(SequenceFileOutputFormat.class);
			job.setJarByClass(PageRank.class);
			job.setMapperClass(pageRandMapper.class);
			job.setCombinerClass(pageRankReducer.class);
			job.setReducerClass(pageRankReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(DoubleWritable.class);
			FileInputFormat.addInputPath(job,
					new Path("Mapreduce" + String.valueOf(initial)));
			FileOutputFormat.setOutputPath(job,
					new Path("Mapreduce" + String.valueOf(initial + 1)));
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
	
	
	public static void sort() {
		Job job;
		try {
			job = Job.getInstance(conf, "PageRank");
			job.setInputFormatClass(SequenceFileInputFormat.class);
			job.setOutputFormatClass(SequenceFileOutputFormat.class);
			job.setJarByClass(PageRank.class);
			job.setMapperClass(sortMapper.class);
			job.setCombinerClass(sortReducer.class);
			job.setReducerClass(sortReducer.class);
			job.setOutputKeyClass(DoubleWritable.class);
			job.setOutputValueClass(Text.class);
			FileInputFormat.addInputPath(job,
					new Path("Mapreduce10"));
			FileOutputFormat.setOutputPath(job,
					new Path("MapreduceResult"));
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

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException,
			URISyntaxException, ClassNotFoundException, InterruptedException {

		InputPaser.Pase();
		for (int i = 0; i < limit; i++) {
			SubPageRank(i);
		}
		sort();
	}
}