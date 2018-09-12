package com.test.hazelcast.jet.stocks;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;

import com.hazelcast.jet.IMapJet;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Traversers;
import com.hazelcast.jet.aggregate.AggregateOperations;
import com.hazelcast.jet.function.DistributedFunctions;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;

public class StockApplication {
	private static String TICKS_MAP_NAME = "ticks_map";

	public static void main(String[] args) throws Exception {
		Pipeline p = Pipeline.create();
		p.drawFrom(Sources.<String>list("text"))
				.flatMap(word -> Traversers.traverseArray(word.toLowerCase().split("\\W+")))
				.filter(word -> !word.isEmpty()).groupingKey(DistributedFunctions.wholeItem())
				.aggregate(AggregateOperations.counting()).drainTo(Sinks.map("counts"));

		// Start Jet, populate the input list
		JetInstance jet = Jet.newJetInstance();
		try {
			List<String> text = jet.getList("text");
			text.add("Mil Pat Mil Pat Mil");
			text.add("Pat Pat Pat Mil");

			// Perform the computation
			jet.newJob(p).join();

			// Check the results
			Map<String, Long> counts = jet.getMap("counts");
			System.out.println("Count of hello: " + counts.get("Mil"));
			System.out.println("Count of world: " + counts.get("Pat"));
		} finally {
			Jet.shutdownAll();
		}

	}

	private static void generateTicks() {
		Executors.newSingleThreadExecutor().submit(()->{
			JetInstance jet = Jet.newJetClient();
			IMapJet<String, Tick> map = jet.getMap(TICKS_MAP_NAME);
			Random random = new Random();
			while(true) {
				map.put("AAPL", new Tick("AAA", Float.valueOf(String.format("%.4f", random.nextFloat()*(100-10)+10))));
				map.put("IBM", new Tick("AAA", Float.valueOf(String.format("%.4f", random.nextFloat()*(100-10)+10))));
				map.put("JPMC", new Tick("AAA", Float.valueOf(String.format("%.4f", random.nextFloat()*(100-10)+10))));
				map.put("BK", new Tick("AAA", Float.valueOf(String.format("%.4f", random.nextFloat()*(100-10)+10))));
				map.put("GOOGL", new Tick("AAA", Float.valueOf(String.format("%.4f", random.nextFloat()*(100-10)+10))));
				map.put("ABC", new Tick("AAA", Float.valueOf(String.format("%.4f", random.nextFloat()*(100-10)+10))));
			}
		});
	}
}
