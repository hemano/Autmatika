package com.autmatika.testing.api.util;

import com.autmatika.testing.api.beans.ActorBean;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Creates a Test.xml which has all the beans of Actors in sequence</p>
 *
 */
public class BeansXmlGenerator {
    String[][] excelBusinessModuleTable = null;

    /**
     * <p>Creates a beans xml that will generate based on user's input in Google Sheet.
     * The order of 'Actors' executed and it's associated parameters will
     * be created in the xml under the Execute Doables
     * Row 0 is ignored for headers. </p>
     *
     * @param testSpreadsheet the test spreadsheet identified by the tester
     * @param testSheet       the test sheet from the test spreadsheet
     * @throws IOException ends test execution if invalid test criteria is identified
     */
    public void xmlGenerate(String testSpreadsheet, String testSheet) throws IOException {

        try {

            // Read from excel file, doable into ref bean attribute
            ExcelGetDataHelper excelData = new ExcelGetDataHelper();

            if (excelBusinessModuleTable == null) {
                excelBusinessModuleTable = excelData.getDataFromExcelBusinessModuleSheet();
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element beans = doc.createElement("beans");

            Attr schemaLocation = doc.createAttribute("xsi:schemaLocation");
            Attr xmlnsXsi = doc.createAttribute("xmlns:xsi");
            Attr xmlns = doc.createAttribute("xmlns");

            beans.setAttributeNode(schemaLocation);
            beans.setAttributeNode(xmlnsXsi);
            beans.setAttributeNode(xmlns);

            schemaLocation.setValue(
                    "http://www.springframework.org/schema/beans   http://www.springframework.org/schema/beans/spring-beans-4.0.xsd");
            xmlnsXsi.setValue("http://www.w3.org/2001/XMLSchema-instance");
            xmlns.setValue("http://www.springframework.org/schema/beans");

            doc.appendChild(beans);

            // RUNNER DOABLE - STATIC

            // bean element for main Executable
            Element bean = doc.createElement("bean");
            // set attributes to the bean Executable
            Attr beanId = doc.createAttribute("id");
            Attr beanClass = doc.createAttribute("class");

            bean.setAttributeNode(beanClass);
            bean.setAttributeNode(beanId);

            beanClass.setValue("com.autmatika.testing.api.impl.ExecuteActor");
            beanId.setValue("ExecuteActor");

            beans.appendChild(bean);

            // elements of Element bean (tag - property)
            Element property = doc.createElement("property");
            Attr propertyName = doc.createAttribute("name");
            property.setAttribute("name", "actors");
            bean.appendChild(property);

            // elements of property - list
            Element list = doc.createElement("list");
            property.appendChild(list);

            // LOOP to put all actors as bean under child tags: list
            // get object into string to use in xml creation into String
            // testDoable
            String[][] excelTestTable = excelData.getDataFromExcelTestSheet(testSpreadsheet, testSheet);


            // This map will contain all of the business modules
            Map<String, List<String>> map = new HashMap<String, List<String>>();

            // BEGIN looping through entire table to capture key value pairs in
            List<ActorBean> doables = null;
            Map<String, List<ActorBean>> doablesMap = new HashMap<String, List<ActorBean>>();
            boolean newBusinessModule = true;
            String currentDoable = null;
            // We start with i = 1 to account for the header row
            int processTablecontextCol = 0;
            int processTableBusinessModuleCol = 1;


            for (int i = 1; i < excelBusinessModuleTable.length; i++) {
                if (newBusinessModule) {
                    currentDoable = excelBusinessModuleTable[i][processTablecontextCol].trim()
                            + excelBusinessModuleTable[i][processTableBusinessModuleCol].trim();
                    doables = new ArrayList<ActorBean>();
                    newBusinessModule = false;
                }
                ActorBean db = new ActorBean(excelBusinessModuleTable[i][2], excelBusinessModuleTable[i][3],
                        excelBusinessModuleTable[i][4], excelBusinessModuleTable[i][5],
                        excelBusinessModuleTable[i][6]);
                doables.add(db);
                // Check if next row exists, and the business module cell is
                // not empty
                if (i + 1 < excelBusinessModuleTable.length
                        && !excelBusinessModuleTable[i + 1][processTableBusinessModuleCol].isEmpty()) {
                    newBusinessModule = true;
                }
                // Check if next cell is a business module
                if (newBusinessModule || i + 1 == excelBusinessModuleTable.length) {
                    doablesMap.put(currentDoable, doables);
                }

            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult(new File("Test.xml"));

            // int doablesColumn from Test Spreadsheet
            int runColumn = 0;
            int testColumn = 1;
            int tagColumn = 2;
            int doablesColumn = 5;
            int contextColumn = 4;
            int businessModuleColumn = 5;

            String test = "";
            String run = "";
            int tempStartRowOfTest = 0;
            boolean firstIteration = true;
            boolean runTest = false;
            boolean lastBusinessModuleInTest = false;
            int iteration = 0;

            String tagValue = "";


            // elements of list - bean
            LogManager.getLogger().debug("EXCEL TEST TABLE LENGTH = " + excelTestTable.length);
            for (int i = 1; i < excelTestTable.length; ) {

                String[] temp = excelTestTable[i];

                if (doablesMap.containsKey(temp[contextColumn].trim() + temp[doablesColumn].trim())) {
                    doables = doablesMap
                            .get(temp[contextColumn].trim() + temp[doablesColumn].trim());
                } else if (doablesMap.containsKey("Base" + temp[doablesColumn].trim())) {
                    doables = (List<ActorBean>) doablesMap.get("Base" + temp[doablesColumn].trim());
                }

                // Check if there is a new test then assign test and catalog to
                // run or not
                if (!(excelTestTable[i][testColumn].length() == 0)) {
                    test = excelTestTable[i][testColumn];
                    run = excelTestTable[i][runColumn];
                    tagValue = excelTestTable[i][tagColumn];
                }

                // Test sheet can have in the run column: blank, Y (run), N
                // (skip), Y# (# denotes how many times test will run)
                String testStatus = "Test In Process";

                if (excelTestTable[i][runColumn].length() > 0
                        && excelTestTable[i][runColumn].substring(0, 1).equalsIgnoreCase("Y")) {
                    testStatus = "Start";
                }

                if (excelTestTable[i][runColumn].length() > 0
                        && excelTestTable[i][runColumn].substring(0, 1).equalsIgnoreCase("Y")
                        && firstIteration) {
                    runTest = true;
                    firstIteration = false;
                    tempStartRowOfTest = i;
                    // if there are iterations are declared after Y store
                    // iteration
                    if (excelTestTable[i][runColumn].trim().length() > 1) {
                        iteration = Integer.parseInt(excelTestTable[i][runColumn].substring(1).trim());
                    } else {
                        iteration = 1;
                    }
                    LogManager.getLogger().debug("Number of iterations for this test = " + iteration);

                } else if (excelTestTable[i][runColumn].length() > 0
                        && excelTestTable[i][runColumn].substring(0, 1).equalsIgnoreCase("N")) {
                    runTest = false;
                }

                // determine if last business module
                if (i + 1 < excelTestTable.length && excelTestTable[i + 1][runColumn].length() > 0
                        || i == excelTestTable.length - 1) {
                    lastBusinessModuleInTest = true;
                    if (iteration == 1) {
                        firstIteration = true;
                    }
                } else {
                    lastBusinessModuleInTest = false;
                }

                String businessModule = excelTestTable[i][businessModuleColumn];

                // create beans for i'th business module if we are running the test
                if (runTest) {
                    LogManager.getLogger().debug("IActor size is: " + doables.size());

                    preprocessActors(doables, excelTestTable, i, doables.size(), test, run, tagValue, testStatus,
                            businessModule, lastBusinessModuleInTest, iteration);

                    generateBeansXml(doables, docFactory, docBuilder, doc, list, iteration);
                }

                i += 1; // actors.size();

                if (!firstIteration && iteration > 1 && lastBusinessModuleInTest) {
                    i = tempStartRowOfTest;
                    iteration--;
                }
            }
            transformer.transform(source, result);

            LogManager.getLogger().debug("BEANS XML created!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

    }
    

    /**
     * <p>This will generate child bean under the list tag belonging to the
     * ExecuteActor bean. This list of beans will determine which doable will
     * be run. This method belongs to a loop that reads the excel file.</p>
     * <p>
     * bean id="ExecuteActor"
     * class="ExecuteActor" property
     * name="actors", ref bean="loginDoable", property
     *
     * @param doc    - xml document this is generating a reference bean for under
     *               the list tag under parent bean tag for Execute IActor
     * @param list   - element that is literally called list that is a tag under
     *               the ExecuteActor
     * @param doable - indexing the excel front end, the business modules are fed
     *               into this method
     * @return beanDoable Element for xml generation
     */
    public Element createBeanList(Document doc, Element list, String doable) {
        // elements of list - bean
        Element beanDoable = doc.createElement("bean");
        beanDoable.setAttribute("class", "com.autmatika.testing.api.impl." + doable.trim() + "Actor");
        list.appendChild(beanDoable);

        return beanDoable;
    }

    /**
     * <p>This will generate child property under the bean residing within the list
     * tag of the ExecuteActor bean.
     * The list of properties will determine which name value pairs will be used.
     * This method belongs to a loop that reads the excel file row and any properties that exist
     *
     * @param doc                 - xml document this is generating a reference bean for under
     *                            the list tag under parent bean tag for Execute IActor
     * @param appendChildLocation - Element of the bean doable on the xml where we want each
     *                            property to be a child element
     * @param name                - the attribute for the key
     * @param value               - the attribute for the value of the key</p>
     */
    public void createPropertyList(Document doc, Element appendChildLocation, String name, String value) {
        // element of bean in list
        Element property = doc.createElement("property");
        property.setAttribute("name", name);
        property.setAttribute("value", value);
        appendChildLocation.appendChild(property);
    }

    /**
     * <p>Build the xml with the following tags for each bean: context, setpId,
     * actorOnFail, elementLocation, parameters, value.
     *
     * @param actors    List, ActorBean, of all actors for business module
     * @param docFactory DocumentBuilderFactory for xml generation
     * @param docBuilder DocumentBuilder for xml generation
     * @param doc        Document for xml generation
     * @param list       Element for xml generation
     * @param iteration  integer used to indicate what itteration we are executing for a repeated test
     *                   </p>
     */
    public static void generateBeansXml(List<ActorBean> actors, DocumentBuilderFactory docFactory,
                                        DocumentBuilder docBuilder, Document doc, Element list, int iteration) {

        BeansXmlGenerator beanList = new BeansXmlGenerator();
        for (ActorBean actor : actors) {
            // Bean
            Element appendChildLocation = beanList.createBeanList(doc, list, actor.getUiAction());

            // Corresponding properties
            beanList.createPropertyList(doc, appendChildLocation, "context", actor.getContext());
            beanList.createPropertyList(doc, appendChildLocation, "stepId", actor.getStepId());
            beanList.createPropertyList(doc, appendChildLocation, "actorOnFail", actor.getActorOnFail());
            beanList.createPropertyList(doc, appendChildLocation, "elementLocation", actor.getElementLocation());
            beanList.createPropertyList(doc, appendChildLocation, "parameters", actor.getParameters());
            beanList.createPropertyList(doc, appendChildLocation, "testOnFail", actor.getTestOnFail());
            beanList.createPropertyList(doc, appendChildLocation, "test", actor.getTest());
            beanList.createPropertyList(doc, appendChildLocation, "run", actor.getRun());
            beanList.createPropertyList(doc, appendChildLocation, "testStatus", actor.getTestStatus());
            beanList.createPropertyList(doc, appendChildLocation, "businessModule", actor.getBusinessModule());
            beanList.createPropertyList(doc, appendChildLocation, "uiAction", actor.getUiAction());
            beanList.createPropertyList(doc, appendChildLocation, "value", actor.getValue());
            beanList.createPropertyList(doc, appendChildLocation, "iteration", actor.getIteration());
            LogManager.getLogger().debug("Bean created for action " + actor.getUiAction());
            beanList.createPropertyList(doc, appendChildLocation, "testTagValue", actor.getTestTagValue());
        }
    }

    /**
     * <p>Sets the doable (action's) value from the test workbook Example: assigns
     * the url to the value of the GoToUrl doable.
     *
     * @param actors
     * @param excelTestTable
     * @param start
     * @param count          </p>
     */
    private void preprocessActors(List<ActorBean> actors, String[][] excelTestTable, int start, int count,
                                  String test, String run, String tagValue, String testStatus, String businessModule, boolean lastBusinessModuleInTest,
                                  int iteration) {
        assert (actors.size() == count);
        int contextCol = 4;
        int firstValueCol = 6;
        int valueColIndex = 0;
        int testOnFailCol = 3;

        String deliminate = "[Parameter]+";
        for (int i = 0; i < count; i++) {
            if (lastBusinessModuleInTest && i == count - 1) {
                testStatus = "End";
            }
            ActorBean doable = actors.get(i);

            if (doable.getParameters().contains("Parameter")) {
                // Deliminate the word Parameter;
                String[] parameterNumber = doable.getParameters().split(deliminate);
                doable.setValue(excelTestTable[start][firstValueCol + valueColIndex]);
                LogManager.getLogger().debug("Set doable with Value: " + excelTestTable[start][firstValueCol + valueColIndex]);
                valueColIndex = Integer.valueOf(parameterNumber[1]);
            }
            doable.setContext(excelTestTable[start][contextCol]);
            doable.setTestOnFail(excelTestTable[start][testOnFailCol]);
            doable.setTest(test);
            doable.setRun(run);
            doable.setTestStatus(testStatus);
            doable.setBusinessModule(businessModule);
            doable.setIteration(Integer.toString(iteration));
            doable.setTestTagValue(tagValue);

            testStatus = "Test in Process";

            LogManager.getLogger().debug("Preprocessing business module " + doable.getBusinessModule() + ". IActor: "
                    + doable.getUiAction() + ". Action: " + (i + 1) + "/" + (count) + ", for test " + doable.getTest()
                    + ", iteration " + iteration);

        }

        LogManager.getLogger().debug("Done with preprocessActors");
        System.out.println("");
    }

}
