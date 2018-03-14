package com.autmatika.testing.api.client;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.IActor;
import com.autmatika.testing.api.PreProcessFiles;
import com.autmatika.testing.api.impl.ExecuteActor;
import com.autmatika.testing.api.util.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StartAction {

    private static final Map<String, String> executionEnvironment = new HashMap<>();
    private static final Object lock = new Object();

    static Map<String, List<IActor>> testAndActorsMap = new LinkedHashMap<>();
    static LinkedHashMap<String, List<IActor>> tagAndTestMap = new LinkedHashMap<>();
    static ThreadLocal<AbstractActor> abstractActor = new ThreadLocal<>();
    static Map<String, List<String>> tagsMap = new LinkedHashMap<>();

    public static void setExecutionEnvironmentInfo() {
        synchronized (lock) {
            if (!executionEnvironment.isEmpty()) {
                return;
            }
            executionEnvironment.put("Operating System", System.getProperty("os.name"));

            String driverName = ((RemoteWebDriver) SeleniumHelper.getInstance().getInitialDriver()).getCapabilities().getBrowserName();
            String driverVersion = ((RemoteWebDriver) SeleniumHelper.getInstance().getInitialDriver()).getCapabilities().getVersion();
            executionEnvironment.put("Browser", driverName + " " + driverVersion);
        }
    }

    public static void main(String[] args) throws Exception {

        // Generate TestBeans.xml from xlsx input & identify files
        PreProcessFiles preProcess = new PreProcessFiles();
        boolean startTestExecution = preProcess.preProcessTestConfiguration(args);

        Reporter.instantiate();
        KeywordsHandler.instantiate();
        MetaDataHandler.instantiate();

        if (startTestExecution) {


            ApplicationContext context = new FileSystemXmlApplicationContext("Test.xml");
            // Run through the Beans xml
            ExecuteActor mainDoable = (ExecuteActor) context.getBean("ExecuteActor");

            List<IActor> doablesList = new ArrayList<>();
            List<IActor> mainDoablesList = mainDoable.getActors();

            /**
             * Creation of test and actors map
             */
            for (int index = 0; index < mainDoablesList.size(); index++) {
                abstractActor.set((AbstractActor) mainDoablesList.get(index));

                if (!abstractActor.get().getTest().equalsIgnoreCase(" ")) {
                    if (!testAndActorsMap.containsKey(abstractActor.get().getTest())) {
                        doablesList = new ArrayList<>();
                        testAndActorsMap.put(abstractActor.get().getTest(), doablesList);
                        doablesList.add(mainDoablesList.get(index));
                    } else {
                        doablesList.add(mainDoablesList.get(index));
                        testAndActorsMap.put(abstractActor.get().getTest(), doablesList);
                    }
                }
            }


            if (System.getProperties().containsKey("tag")) {

                List<String> testList = new ArrayList<>();
                for (Map.Entry<String, List<IActor>> entry : testAndActorsMap.entrySet()) {

                    String tagValueOfTest = ((AbstractActor) entry.getValue().get(0)).getTestTagValue().toUpperCase();

                    if (!tagsMap.containsKey(tagValueOfTest)) {
                        testList = new ArrayList<>();
                        testList.add(entry.getKey());
                        tagsMap.put(tagValueOfTest.toUpperCase(), testList);

                    } else {
                        tagsMap.get(tagValueOfTest.toUpperCase()).add(entry.getKey());
                    }
                }

                tagsMap.remove("");

//                Adding sheets Map to Tags map
                Map<String, List<String>> sheetAndTestsMap = ExcelGetDataHelper.getMapOfSheetAndTests();
                tagsMap.putAll(sheetAndTestsMap);
                tagsMap = tagsMap.entrySet().stream().collect(Collectors.toMap(e->e.getKey().toUpperCase(), e->e.getValue()));

                String userTag = System.getProperty("tag").toUpperCase();
                String[] givenTags = userTag.split(";");


                //Creating a set filtering out the test cases
                Set<String> parallelTestsSet = new HashSet<>();
                Set<String> serialTestsSet = new HashSet<>();

                for (String tag : givenTags) {
                    tag = tag.toUpperCase();
                    if (tag.endsWith("(S)") || tag.endsWith("(s)")) {
                        serialTestsSet.addAll(tagsMap.get(tag.substring(0, tag.indexOf("(")).toUpperCase()));
//                        serialTestsSet.addAll(tagsMap.get(tag.toUpperCase()));
                    }

                    if (tagsMap.keySet().contains(tag) && !(tag.endsWith("(S)") || tag.endsWith("(s)"))) {
                        parallelTestsSet.addAll(tagsMap.get(tag.toUpperCase()));
                    }

                    if (serialTestsSet.size() < 1 && parallelTestsSet.size() < 1) {
                        throw new Exception("No test enabled with the specified tag\n" +
                                "Tags associated with enabled tests: " + tagsMap.keySet().toString() + "\n" +
                                "User given tag: " + tag);
                    }
                }


                //Seperate Parallel and Serial tests
                List<IActor> serialTestActors = new ArrayList<>();
                List<String> testListToSerialize = new ArrayList<>();

                for (String test : serialTestsSet) {
                    serialTestActors.addAll(testAndActorsMap.get(test));
                }

//                for (Map.Entry entry :testAndActorsMap.entrySet()) {
//                    testListToSerialize.add(entry.getKey().toString());
//                    serialTestActors.addAll((ArrayList)entry.getValue());
//                }

                testAndActorsMap.keySet().retainAll(parallelTestsSet);

                if(!serialTestActors.isEmpty()){
                    testAndActorsMap.put("Serial", serialTestActors);
                }

//                for (String testName:testListToSerialize) {
//                    testAndActorsMap.remove(testName);
//                }


            }


            ExecutorService executor = Executors.newFixedThreadPool(7);

            for (Map.Entry<String, List<IActor>> entry : testAndActorsMap.entrySet()) {
                Runnable runnable = new ExecuteActor(entry.getValue());
                executor.execute(runnable);
            }


            try {
                executor.shutdown();
                executor.awaitTermination(2, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            System.out.println("\t\t\t\t\t\t\tEXECUTIONS HAS FINISHED");
            Reporter.setSystemInfo(executionEnvironment);

            if (!Reporter.getBuildStatus()) {
                throw new RuntimeException("Build failed");
            }
        }

    }


}