/* -------------------------------------------------------------------------- */
/*                                                                            */
/*               LUCS-KDD DATA DISCRETISATION/NORMALISATION                   */
/*                 (DATA PREPARATION GUI FOR DATA MINING)                     */
/*                                                                            */
/*                              Frans Coenen                                  */
/*                                                                            */
/*                          Monday 15 June 2003                               */
/*             (Revised: 17 Feb 2004, 27 July 2007, 4 March 2009)             */
/*                                                                            */
/*                    Department of Computer Science                          */
/*                     The University of Liverpool                            */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/* The LUCS-KDD (Liverpool University Computer Science - Knowledge Discovery in
Data) DN (discretization/normalisation) software has been developed to convert
data files available in the UCI data repository (http://www.ics.uci.edu/~mlearn/
MLRepository.html) into a binary format suitable for use with Association Rule
Mining (ARM) and (Classification Association Rule Mining (CARM) software. The
software can, of course, equally well be used to convert data files obtained
from other sources. */
package anomaly.ltu.se;
import java.io.IOException;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Other packages

public class LUCS_KDD_DN_ARM extends JFrame implements ActionListener{

    /* ------ FIELDS ------ */

    /* GUI features  */

    // BUTTONS
    // Input buttons
    /** Input data button (space separated). */
    private JButton inputSpaceDataButton;
    /** Input data button (comma separated). */
    private JButton inputCommaDataButton;
    /** Input schema. */
    private JButton inputInputSchemaButton ;
    /** Input max range. */
    private JButton inputNumRangesButton;

    // Output Input Buttons
    /** List input data */
    private JButton listInputDataButton;
    /** List schema */
    private JButton listInputSchemaButton;
    /** List Min Max data */
    private JButton listMinMaxDataButton;

    // Edit Buttons
    /** Remove column N */
    private JButton removeColNbutton;
    /** Move column N to end */
    private JButton moveNtoEndButton;
    /** Move column N to before col M */
    private JButton moveNtoMbutton;
    /** Randomise */
    private JButton randomizeRowsButton;

    // Normalisation Buttons
    /** Normalisation */
    private JButton normaliseButton;
    /** List output data */
    private JButton listOutputDataButton;
    /** List output schema */
    private JButton listOutputSchemaButton;
    /** List parameters */
    private JButton listParametersButton;
    /** Save normalisation button */
    private JButton saveNormButton;
    /** Save output schema button */
    private JButton saveOutputSchemaButton;

    // Panels
    /** Input buttons panel */
    private JPanel inputPanel;
    /** Input output buttons panel */
    private JPanel inputOutputPanel;
    /** Edit buttoms panel */
    private JPanel editPanel;
    /** Nor,alisation buttoms panel */
    private JPanel normalisationPanel;
    // Credits panel */
    JPanel creditsPanel;

    // Other components
    /** Text Area */
    private JTextArea textArea;

    // Data structures
    /** 2-D array to hold input data from data file */
    private double[][] inputDataArray = null;
    /** 2-D array to hold (normalised) output data  */
    private int[][] outputDataArray = null;
    /** Holds min, max and range data for input. */
    private double[][] minMaxData = null;
    /** Holds schema as contained in schema file */
    private String[][] schema = null;
    /** Holds nominal values, will be null in the case of non-nominal values. */
    private String[][] nominalData = null;

    // Instance fields
    /** Instance of BufferedReader class */
    private BufferedReader fileInput;
    /** Instance of PrintWriter class */
    private PrintWriter fileOutput;

    // Flags
    /** Flag to indicate whether system has data or not. */
    private boolean hasDataFlag = false;
    /** Flag to indicate whether system has data or not. */
    private boolean hasInputSchemaFlag = false;
    /** Flag to indicate whether system has data or not. */
    private boolean hasMaxRangeFlag = false;
    /** Flag to indicate whether system has normalisation results. */
    private boolean hasNormalisationFlag = false;
    /** Unrecognised nominal value. */
    private boolean unrecognisedNominalValue = false;
    /** Ouput schema flag. */
    private boolean outputSchemaFlag = false;

    // Constants
    /** Missing term value 100,000,001 (less than test term!). */
    private final double MISSING_ITEM = -100000001.0;
    /** Term against which missing term value is tested 100,000,000. */
    private final double TEST_TERM    = -100000000.0;

    // Other fields
    /** Input data file name. */
    private File inputFileName;
    /** Output data file name. */
    private File outputFileName;
    /** Output schema file name. */
    private File outputInputSchemaFileName;
    /** Number of rows. */
    private int numRows  = 0;
    /** Number of Columns in input data. */
    private int numColsInInputData  = 0;
    /** Number of Columns in input schema. */
    private int numColsInInputSchema  = 0;
    /** Number of Columns in normalisation data. */
    private int numColsInNormalisationData  = 0;
    /** The maximum number of integers that can be defined discretely */
    private int maxRange = 5;
    /** Number of attributes in output data set (set during normalisation). */
    private int numAttributes = 0;
    /** Number of missing items */
    private int numMissingItems = 0;

    /* --------------------------------------------------- */
    /*                                                     */
    /*                    CONSTRUCTORS                     */
    /*                                                     */
    /* --------------------------------------------------- */

    /** One argument constructor.
    @params The window name. */

    public LUCS_KDD_DN_ARM(String s) {
        super(s);

	// Content pane
        Container container = getContentPane();
        container.setBackground(Color.pink);
        container.setLayout(new BorderLayout(5,5)); // 5 pixel gaps

        // Creata panels
        createInputPanel();
        createInputOutputPanel();
        createEditPanel();
        createNormalisationPanel();

        // Credits Panel
	createCreditsPanel();

	// Add button Panel
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new GridLayout(1,4));
	buttonPanel.add(inputPanel);
	buttonPanel.add(inputOutputPanel);
	buttonPanel.add(editPanel);
	buttonPanel.add(normalisationPanel);
	container.add(buttonPanel,BorderLayout.NORTH);

	// Add text area
	textArea = new JTextArea(40, 15);
	textArea.setEditable(false);
        container.add(new JScrollPane(textArea),BorderLayout.CENTER);
        instructions();

	// Add credits pabnel
	container.add(creditsPanel,BorderLayout.SOUTH);
	}

    /* CREATE CREDITS PANEL */
    /** Create credits panel. */

    private void createCreditsPanel() {
	// Swet up panel
	creditsPanel = new JPanel();
	creditsPanel.setBackground(Color.pink);
	creditsPanel.setLayout(new GridLayout(4,1));

	// Create labels
	Label creditLabel1 = new Label("LUCS-KDD (Liverpool University Computer " +
				"Science - Knowledge Discovery");
	Label creditLabel2 = new Label("in Data) group group DN software (ARM data).");
	Label creditLabel3 = new Label(" ");
	Label creditLabel4 = new Label("Created by Frans Coenen (15 July " +
				"2003)");

	// Add labels
	creditsPanel.add(creditLabel1);
	creditsPanel.add(creditLabel2);
	creditsPanel.add(creditLabel3);
	creditsPanel.add(creditLabel4);
	}

    /* CREAT INPUT PANEL */
    /** Sets up input panel for interface. */

    private void createInputPanel() {
	// Set Up panel
	inputPanel = new JPanel();
	inputPanel.setBackground(Color.pink);
	inputPanel.setLayout(new GridLayout(4,1));

	// Create buttons
    	inputInputSchemaButton = new JButton("Input InputSchema");
     	inputInputSchemaButton.addActionListener(this);
  	inputCommaDataButton = new JButton("Input Data (',' Sep.)");
   	inputCommaDataButton.setEnabled(false);
   	inputCommaDataButton.addActionListener(this);
	inputSpaceDataButton = new JButton("Input Data (' ' Sep.)");
 	inputSpaceDataButton.setEnabled(false);
 	inputSpaceDataButton.addActionListener(this);
        inputNumRangesButton = new JButton("Input Num. Ranges"); ;
        inputNumRangesButton.setEnabled(true);
        inputNumRangesButton.addActionListener(this);

      	// Add to panel
        inputPanel.add(inputInputSchemaButton);
       	inputPanel.add(inputSpaceDataButton);
        inputPanel.add(inputCommaDataButton);
	inputPanel.add(inputNumRangesButton);
	}

    /* CREATE INPUT OUTPUT PANEL */
    /** Sets up input output panel. */

    private void createInputOutputPanel() {
	// Set Up panel
	inputOutputPanel = new JPanel();
	inputOutputPanel.setBackground(Color.pink);
	inputOutputPanel.setLayout(new GridLayout(3,1));

	// Create buttons
	listInputDataButton = new JButton("List Input Data");
        listInputDataButton.addActionListener(this);
	listInputDataButton.setEnabled(false);
        listInputSchemaButton = new JButton("List InputSchema");
        listInputSchemaButton.addActionListener(this);
	listInputSchemaButton.setEnabled(false);
	listMinMaxDataButton = new JButton("List Min Max Data");
	listMinMaxDataButton.addActionListener(this);
	listMinMaxDataButton.setEnabled(false);

        // Add to panel
       	inputOutputPanel.add(listInputDataButton);
       	inputOutputPanel.add(listInputSchemaButton);
       	inputOutputPanel.add(listMinMaxDataButton);
	}

    /* CREATE EDIT PANEL */
    /** Sets up outpu panel. */

    private void createEditPanel() {
	// Set Up panel
	editPanel = new JPanel();
	editPanel.setBackground(Color.pink);
	editPanel.setLayout(new GridLayout(4,1));

	// Create buttons
        moveNtoEndButton = new JButton("Move N to End");
	moveNtoEndButton.addActionListener(this);
	moveNtoEndButton.setEnabled(false);
        moveNtoMbutton = new JButton("Move N to Before M");
	moveNtoMbutton.addActionListener(this);
	moveNtoMbutton.setEnabled(false);
        removeColNbutton = new JButton("Remove Col N");
	removeColNbutton.addActionListener(this);
	removeColNbutton.setEnabled(false);
        randomizeRowsButton = new JButton("Randomize Rows");
	randomizeRowsButton.addActionListener(this);
	randomizeRowsButton.setEnabled(false);

	// Add to panel
	editPanel.add(moveNtoEndButton);
	editPanel.add(moveNtoMbutton);
	editPanel.add(removeColNbutton);
	editPanel.add(randomizeRowsButton);
        }

    /* CREATE NORMALISATION PANEL */
    /** Sets up normalisation tools panel. */

    private void createNormalisationPanel() {
	// Set Up panel
	normalisationPanel = new JPanel();
	normalisationPanel.setBackground(Color.pink);
	normalisationPanel.setLayout(new GridLayout(6,1));

	// Create buttons
	normaliseButton = new JButton("Normalise");
	normaliseButton.addActionListener(this);
	normaliseButton.setEnabled(false);
	listOutputSchemaButton = new JButton("List Output Schema");
	listOutputSchemaButton.addActionListener(this);
	listOutputSchemaButton.setEnabled(false);
	listParametersButton = new JButton("List Parameters");
	listParametersButton.addActionListener(this);
	listParametersButton.setEnabled(false);
        listOutputDataButton = new JButton("List Output Data");
        listOutputDataButton.addActionListener(this);
	listOutputDataButton.setEnabled(false);
	saveNormButton = new JButton("Save Normalization");
	saveNormButton.addActionListener(this);
	saveNormButton.setEnabled(false);
        saveOutputSchemaButton = new JButton("Save Output Schema");
	saveOutputSchemaButton.addActionListener(this);
	saveOutputSchemaButton.setEnabled(false);

        // Add to panel
       	normalisationPanel.add(normaliseButton);
	normalisationPanel.add(listParametersButton);
	normalisationPanel.add(listOutputSchemaButton);
	normalisationPanel.add(listOutputDataButton);
	normalisationPanel.add(saveNormButton);
	normalisationPanel.add(saveOutputSchemaButton);
	}

    /* ATION PERFORMED */
    /* Action listeners
    @param event The triggered event. */
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("Input Data (' ' Sep.)")) inputSpaceData();
        if (event.getActionCommand().equals("Input Data (',' Sep.)")) inputCommaData();
	if (event.getActionCommand().equals("Input InputSchema")) inputInputSchema();
	if (event.getActionCommand().equals("Input Num. Ranges")) inputNumRanges();
        if (event.getActionCommand().equals("Normalise")) normalise();
        if (event.getActionCommand().equals("List Input Data")) listInputData();
        if (event.getActionCommand().equals("List InputSchema")) listInputSchema();
        if (event.getActionCommand().equals("List Output Data")) listOutputData();
        if (event.getActionCommand().equals("List Min Max Data")) listMinMaxData();
        if (event.getActionCommand().equals("List Parameters")) listParameters();
        if (event.getActionCommand().equals("List Output Schema")) outputOutputSchema();
        if (event.getActionCommand().equals("Save Normalization")) saveNormalisation();
        if (event.getActionCommand().equals("Save Output Schema")) saveOutputSchema();
        if (event.getActionCommand().equals("Move N to End")) moveColNtoEnd();
        if (event.getActionCommand().equals("Move N to Before M")) moveColNtoBeforeColM();
        if (event.getActionCommand().equals("Remove Col N")) removeColN();
        if (event.getActionCommand().equals("Randomize Rows")) randomizeRows();

        // Reset buttonn enabling
	resetButtonEnabling();
	}

    /* RESET BUTTON ENABLING */
    /* Resets the functionality of the GUI by enabling/disabling buttons. */

    private void resetButtonEnabling() {
        if (hasInputSchemaFlag) {
            listInputSchemaButton.setEnabled(true);
            inputCommaDataButton.setEnabled(true);
 	    inputSpaceDataButton.setEnabled(true);
	    if (hasDataFlag) {
	        determinMaxMimDataArray();
	        listMinMaxDataButton.setEnabled(true);
		}
            }
        if (hasDataFlag) {
            listInputDataButton.setEnabled(true);
            moveNtoEndButton.setEnabled(true);
	    moveNtoMbutton.setEnabled(true);
	    removeColNbutton.setEnabled(true);
	    randomizeRowsButton.setEnabled(true);
	    if (hasInputSchemaFlag) {
	        determinMaxMimDataArray();
	        listMinMaxDataButton.setEnabled(true);
		}
            }
        if (hasMaxRangeFlag & hasDataFlag) normaliseButton.setEnabled(true);
        if (hasNormalisationFlag) {
            listOutputDataButton.setEnabled(true);
            listParametersButton.setEnabled(true);
            listOutputSchemaButton.setEnabled(true);
            saveNormButton.setEnabled(true);
	    saveOutputSchemaButton.setEnabled(true);
            }
        }

    /** Resetys button to start mode when new schedule is loaded. */
    
    private void resetButtonsToStart() {
        // Button
	inputCommaDataButton.setEnabled(false);
   	inputSpaceDataButton.setEnabled(false);
	listInputDataButton.setEnabled(false);
        listInputSchemaButton.setEnabled(false);
	listMinMaxDataButton.setEnabled(false);
	moveNtoEndButton.setEnabled(false);
        moveNtoMbutton.setEnabled(false);
        removeColNbutton.setEnabled(false);
        randomizeRowsButton.setEnabled(false);
	normaliseButton.setEnabled(false);
	listOutputSchemaButton.setEnabled(false);
	listParametersButton.setEnabled(false);
        listOutputDataButton.setEnabled(false);
	saveNormButton.setEnabled(false);
	saveOutputSchemaButton.setEnabled(false);
    
        // Data structures
	inputDataArray = null;
        outputDataArray = null;
	
	// Flags
	hasDataFlag = false;
        hasNormalisationFlag = false;
	unrecognisedNominalValue = false;
        outputSchemaFlag = false;
    
	// Other variables
	numRows  = 0;
        numColsInInputData  = 0;
        numColsInNormalisationData  = 0;
        numAttributes = 0;
        numMissingItems = 0;
	}
  
    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                           INPUT DATA                             */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /* INPUT SPACE DATA */
    /** Commences process of reading input file of space separated numbers:
    (1) displays file dialog box, (2) checks the selected file is readable
    and if so (3) reads the file.*/
    private void inputSpaceData() {
        textArea.append("READING INPUT FILE:\n-------------------\n");
	textArea.append("Space separated attributes\n" );

	inputData(" ");
	}

    /* INPUT COMMA DATA */
    /** Commences process of reading input file of comma separated numbers:
    (1) displays file dialog box, (2) checks the selected file is readable
    and if so (3) reads the file.*/
    private void inputCommaData() {
        textArea.append("READING INPUT FILE:\n-------------------\n");
	textArea.append("Comma separated attributes\n" );

        inputData(",");
        }

    /* Input DATA */
    /** Commences process of reading input file: (1) displays file dialog box,
    (2) checks the selected file is readable and if so (3) reads the file and
    determines ranges.
    @param discriminator the separator between numbers, e.g. space or comma. */

    private void inputData(String discriminator) {
	// Display file dialog so user can select file to open
	JFileChooser fileChooser = new JFileChooser();
	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	int result = fileChooser.showOpenDialog(this);

	// If cancel button selected return
	if (result == JFileChooser.CANCEL_OPTION) return;

	// Obtain selected file
	inputFileName = fileChooser.getSelectedFile();
	textArea.append("inputFileName = " +  inputFileName + "\n");
	// Read file if readabale (i.e not a direcrory etc.).
	if (checkFileName()) readDataFile(discriminator);
	}

    /* READ DATA FILE */
    /** Reads the selected input file: (1) checks file format and if OK determines
    the number of records in the file,  and then (if OK) (2) dimensions the data
    array in which the file is to be stored, (3) causes the data to be loaded into
    the data array and (4) isets the <TT>hasDataFlag</TT> to "true".
    @param discriminator the separator between numbers, e.g. space or comma. */

    private void readDataFile(String discriminator) {

	try {
	    // If file OK
	    if (countNumRecordsAndCols(discriminator)) {
	        // Dimension input data array
	        inputDataArray  = new double[numRows][];
	        // Read file
	        if (inputDataSet(discriminator)) {
  	            // Set have data flag to true
	            hasDataFlag = true;
	            textArea.append("Number of records = " + numRows +
	                      "\nNumber of columns = " + numColsInInputData +
	                      "\nNum. missing values = " + numMissingItems +
	                      "\n\n");
	            }
		}
	    else return;
	    }
	catch(IOException ioException) {
	    JOptionPane.showMessageDialog(this,"Error reading File",
			 "Error: ",JOptionPane.ERROR_MESSAGE);
	    closeInputFile();
	    System.exit(1);
	    }
	}

    /* COUNT NUMBER OF RECORDS AND COLUMNS */
    /** Reads the input file and dertermines the number of records and columns
    @param discriminator the separater between numbers, e.g. spaxce or comma.
    @return false if file is not in correct format or not of correct size. */

    private boolean countNumRecordsAndCols(String discriminator) throws IOException {
        int counter = 0;
        int localNumCols =0 ;
	boolean firstLine = true;

	// Open the file
	openInputFile();

	// Loop through file incrementing counter
	// get first row and check disccriminator, if not of correct type return false
	String line = fileInput.readLine();
	if (line.indexOf(discriminator.charAt(0)) < 0) {
	    if (discriminator.equals(" ")) JOptionPane.showMessageDialog(null,
	    		"File is not space separated\n\n" );
	    if (discriminator.equals(",")) JOptionPane.showMessageDialog(null,
	    		"File is not Comma separated\n\n" );
            closeInputFile();
	    return(false);
	    }

	// Loop through file
	while (line != null) {
	    StringTokenizer dataLine = new StringTokenizer(line,discriminator);
            int numberOfTokens = dataLine.countTokens();
	    if (firstLine) {
	        localNumCols=numberOfTokens;
	        firstLine=false;
	        }
	    if (numberOfTokens == 0) break;
	    counter++;
            line = fileInput.readLine();
	    }

	// Check number of columns with that in schema file (if it exists)
	if (numColsInInputSchema != localNumCols) {
	    JOptionPane.showMessageDialog(null,
	            	"ERROR: Number of columns in input data\n" +
	            	"(" + localNumCols + ") is not the same as in the schema file " +
	            	"(" + numColsInInputSchema + ")\n");
            numRows = counter;
	    numColsInInputData = localNumCols;
	    closeInputFile();
            return(false);
	    }

	// Set number of rows and columns and close file
	numRows = counter;
	numColsInInputData = localNumCols;
        closeInputFile();
        return(true);
	}

    /* INPUT DATA SET */
    /** Reads input data from file specified in command line argument and
    places data in inputDataArray.
    @param discriminator the separator between numbers, e.g. space or comma.
    @return true if data successfully loaded, false otherwise. */

    public boolean inputDataSet(String discriminator) throws IOException {
        int rowIndex=0;

	// Open the file
	openInputFile();

	// get first row.
	String line = fileInput.readLine();

	// Loop until empty line found
	while (line != null) {
	    // Create a StringTokienizer instance
	    StringTokenizer dataLine = new StringTokenizer(line,discriminator);
            // Get number of tokens
            int numberOfTokens = dataLine.countTokens();
	    // If no tokens jump out of loop
	    if (numberOfTokens == 0) break;
	    // Otherwise convert input string to a sequence of doubles
	    double[] code = doubleConversion(rowIndex+1,dataLine,
	    			numberOfTokens);
	    // Check for unrecognised nominal value error
	    if (unrecognisedNominalValue) {
	    	textArea.append("ABORTING READ OPERATION!");
		unrecognisedNominalValue=false;
		closeInputFile();
		return(false);
		}
            // Check for "null" input
	    if (code!=null) {
	    	// Dimension rows in 2-D inputDataArray
		int codeLength = code.length;
		inputDataArray[rowIndex] = new double[codeLength];
		// Assign to elements in row
		for (int colIndex=0;colIndex<codeLength;colIndex++)
			inputDataArray[rowIndex][colIndex] = code[colIndex];
		}
	    else inputDataArray[rowIndex]=null;
	    // Increment first index in 2-D data array
	    rowIndex++;
	    // get next line
            line = fileInput.readLine();
	    }

	// Close file
	closeInputFile();
	return(true);
	}

    /* DOUBLE CONVERSION. */
    /** Produce an array of doubles from the input line.
    @param lineNum line number in input data file
    @param dataLine row from the input data file
    @param numberOfTokens number of items in row
    @return 1-D array of doubles representing attributes in input row */

    private double[] doubleConversion(int lineNum, StringTokenizer dataLine,
    							int numberOfTokens) {
        double[] newItemSet = null;

	// Loop through input line and add contents to data array
	for (int tokenCounter=0;tokenCounter < numberOfTokens;tokenCounter++) {
	    String s1 = dataLine.nextToken();
	    double number;
	    // Test for missing term
	    if (s1.equals("?")) {
	    	numMissingItems++;
	    	number = MISSING_ITEM;
	    	}
	    else {
	        // Test for unused value
		if (schema[0][tokenCounter].equals("unused")) number=0;
	        // Test for nominal value if so determine value
	        else if (schema[0][tokenCounter].equals("nominal")) {
	            number = getNominalValue(lineNum,s1,tokenCounter);
	            if (unrecognisedNominalValue) return(null);
		    }
	        // Otherwise number (could be an integer or a doble but treat
		// both the same
	        else number = Double.parseDouble(s1);
		}
	    // Enter value into data array
	    newItemSet = realloc1(newItemSet,number);
	    }

	// Return itemSet
	return(newItemSet);
	}

    /* GET NOMINAL VALUE */
    /** Get nominal value.
    @param lineNum line number in input data file
    @param s1 the string read from the input stream
    @param colNum the column number
    @return an integer returned as a double representing the nominal value. */

    private double getNominalValue(int lineNum, String s1, int colNum) {
        unrecognisedNominalValue=false;

        // Loop through possible values for this column looking for a match
        for(int index=0;index<nominalData[colNum].length;index++) {
            if (nominalData[colNum][index].equals(s1)) return(index);
            }

        // Check for missing value
	if (s1.equals("?")) return(MISSING_ITEM);

	// End, if process gets this far no comparable value has been found
        // in which case there is something wrong with either the schema or
        // the data.
        textArea.append("ERROR in line " + lineNum + " of input data: data " +
		"item \"" + s1 + "\" not found in schema entry for column " +
		(colNum+1) + ", options are: { ");
        for (int index=0;index<nominalData[colNum].length;index++) {
            textArea.append(nominalData[colNum][index] + " ");
            }
        textArea.append("}\n");
	unrecognisedNominalValue=true;
        return(MISSING_ITEM); 	// Error value
        }

    /* DETERMINE RANGES */
    /** Determine range of values for each column. */

    private void determineRanges() {

	// process min/max array
	for (int colIndex=0;colIndex<minMaxData[0].length;colIndex++) {
	    minMaxData[2][colIndex] = minMaxData[1][colIndex] -
	    					minMaxData[0][colIndex];
	    }
	}

    /* DETERMINE MIN, MAX AND RANGE VALUES */
    /** Determines minimum, maximum and range array values. <P> Operates
    only when both schema and data exist. Also called when a column is
    moved or deleted. First two values in minMaxData array are the minimum
    and maximum value for the column. Last value is the range. */

    private void determinMaxMimDataArray() {
        if (!hasInputSchemaFlag) return;

        // Dimension minMax data array and initilise each element to missing
	// term value.
        minMaxData = new double[3][numColsInInputData];
	for(int index=0;index<minMaxData[0].length;index++) {
	    minMaxData[0][index] = MISSING_ITEM;
	    minMaxData[1][index] = MISSING_ITEM;
	    }

        // Loop through input data array
        for (int rowIndex=0;rowIndex<inputDataArray.length;rowIndex++) {
            for (int colIndex=0;colIndex<inputDataArray[rowIndex].length;colIndex++) {
	        // If nominal value add values from schema file
		if (schema[0][colIndex].equals("nominal")) {
		    minMaxData[0][colIndex] = 0;
		    minMaxData[1][colIndex] = nominalData[colIndex].length-1;
		    minMaxData[2][colIndex] = nominalData[colIndex].length-1;
		    }
		// Otherwise process
		else {
		    // Update minMax data if data item is not a "missing item"
		    // otherwise leave min max entries with current values
	            if (inputDataArray[rowIndex][colIndex]>TEST_TERM) {
	                // Test for no previous values
			if (minMaxData[0][colIndex] < TEST_TERM) {
	                    minMaxData[0][colIndex] =
			    		inputDataArray[rowIndex][colIndex];
		            minMaxData[1][colIndex] =
			    		inputDataArray[rowIndex][colIndex];
		            }
	                else {
			    // Test if input less than current minimum
	                    if (inputDataArray[rowIndex][colIndex] <
			    		minMaxData[0][colIndex])
					minMaxData[0][colIndex] =
					inputDataArray[rowIndex][colIndex];
			    // Test if input greater than current maximum
		            if (inputDataArray[rowIndex][colIndex] >
			    		minMaxData[1][colIndex])
			    		minMaxData[1][colIndex] =
					inputDataArray[rowIndex][colIndex];
		            }
			}
		    }
		}
	    }

	// Determine ranges
	determineRanges();
	}

    /* RECALCULATE NUMBER OF MISSING ITEMS */
    /** Recalcualtes the number of missing items in the input data set (used
    when a column has been removed. */

    private void recalculateMissingItems() {
        numMissingItems=0;

        // Loop through data array
        for(int index1=0;index1<inputDataArray.length;index1++) {
            for (int index2=1;index2<inputDataArray[index1].length;index2++) {
                if (inputDataArray[index1][index2]<TEST_TERM) numMissingItems++;
                }
            }
        }

    /* ------------------------------------------------- */
    /*                                                   */
    /*                    INPUT SCHEMA                   */
    /*                                                   */
    /* ------------------------------------------------- */

    /* INPUT SCHEMA */
    /** Commences process of inputting data schema, if schema not read (for whatever reason) sets
    hasInputSchemaFlag value to false. */

    private void inputInputSchema() {
        textArea.append("READING INPUT SCHEMA:\n-----------------------\n");

	// Display file dialog so user can select file to open
	JFileChooser fileChooser = new JFileChooser();
	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	int result = fileChooser.showOpenDialog(this);

	// If cancel button selected return
	if (result == JFileChooser.CANCEL_OPTION) return;

	// Obtain selected file
	inputFileName = fileChooser.getSelectedFile();
	textArea.append("InputSchema inputFileName = " +  inputFileName + "\n\n");
	// Read file if readabale (i.e not a direcrory etc.).
	if (checkFileName()) readInputSchemaFile();
	else  hasInputSchemaFlag=false;
        }

    /* READ SCHEMA DILE  */
    /** Reads schema file and stores in 2-D array.
    First row is "unused", "nominal", "int" or "double"
    */

    private void readInputSchemaFile() {

	try {
	    // Open the file
	    openInputFile();
	    // Read first line and tokenise
	    String lineFromFile = fileInput.readLine();
	    StringTokenizer dataLine = new StringTokenizer(lineFromFile);
	    // Get numberof columns in schema file
	    numColsInInputSchema =  dataLine.countTokens();
	    // Diemsion schema and nominal data arrays
            schema = new String[2][numColsInInputSchema];
            nominalData = new String[numColsInInputSchema][];
	    // Store first line in schema array
	    for (int colIndex=0;colIndex<numColsInInputSchema;colIndex++)
	            		schema[0][colIndex] = dataLine.nextToken();
	    // Read reast of schema file
	    if (readRestOfInputSchemaFileOK()) {
	        if (hasInputSchemaFlag) {
		    textArea.append("New schema");
		    resetButtonsToStart();
		    }
		hasInputSchemaFlag=true;
		}
	    else hasInputSchemaFlag=false;
	    // Close file
	    closeInputFile();
	    }
	catch (IOException ioException) {
	    JOptionPane.showMessageDialog(this,"Error reading File",
			 "Error: ",JOptionPane.ERROR_MESSAGE);
	    closeInputFile();
	    System.exit(1);
	    }
	}

    /* READ REST OF SCEMA FILE OK */
    /** Reads second and third lines from schema file. If OK returns
    true, otherwise returns false.
    @return True if schema is OK and false otherwise. */

    private boolean readRestOfInputSchemaFileOK() throws IOException {
        // Read second line and tokenise
        String lineFromFile = fileInput.readLine();
	StringTokenizer dataLine = new StringTokenizer(lineFromFile);

	// If not same length as first line in schema return false.
	if (!sameLengthAsFirstLineInScema(dataLine,"second")) return(false);

	// If OK store in schema array data structure
	for (int colIndex=0;colIndex<numColsInInputSchema;colIndex++)
	                	schema[1][colIndex] = dataLine.nextToken();

	// Check contents of first line of schema
	if (!checkInputSchemaContents()) return(false);

	// If OK read third line from schema
	lineFromFile = fileInput.readLine();
	dataLine = new StringTokenizer(lineFromFile);

	// If not same length as first line in schema return false.
	if (!sameLengthAsFirstLineInScema(dataLine,"third")) return(false);

	// Load nominal data array
	loadNominalDataArray(dataLine);

	// Check contents of third line of schema
	return(checkNominalData());
        }

    /* LOAD NOMINAL DATA ARRAY */
    /** Loads nominal data array with possible values for nominal data attributes, "null" for
    other attributes. */

    private void loadNominalDataArray(StringTokenizer dataLine) {
        numColsInNormalisationData = numColsInInputSchema;

        // Loop through second line of scema data array to identify nominal attributes
        for (int colIndex=0;colIndex<schema[0].length;colIndex++) {
            // get item from third line of schema file
            String itemList = dataLine.nextToken();
            // Check scema type value
            if (schema[0][colIndex].equals("nominal")) {
                StringTokenizer itemListTokens = new StringTokenizer(itemList,"/");
                // Determine number of values in item list
                int numberOfValues = itemListTokens.countTokens();
                // Dimension line in nominal data array
                nominalData[colIndex] = new String[numberOfValues];
                // loop through item list of nominal values seperated by '/' charactor.
                for (int tokenCounter=0;tokenCounter<numberOfValues;tokenCounter++)
                    		nominalData[colIndex][tokenCounter] = itemListTokens.nextToken();
                }
    	    else nominalData[colIndex]=null;
    	    }
        }

    /* SAME LENGTH AS FIRST LINE IN SCHEMA */
    /** Checks that second line in schema has same number of tokens as first line
    @param dataLine The input line from the schema file
    @return type is same number of tokens, and false otherwise. */

    private boolean sameLengthAsFirstLineInScema(StringTokenizer dataLine, String lineNumber) {
        int localLength =  dataLine.countTokens();

        if (localLength != numColsInInputSchema)  {
	    JOptionPane.showMessageDialog(null,
	            	"ERROR: Number of columns in " + lineNumber + " line \n" +
	            	"of schema (" + localLength + ") is not the\nsame as in first line " +
	            	"(" + numColsInInputSchema + ")\n");
	    return(false);
	    }

	// End
	return(true);
        }

    /* CHECK SCHEMA CONTENTS. */
    /** Checks first line of schema file and outputs error message if an entry
    is found that is not "int", "double", "nominal" or "unused".
    @return true if OK, false otherwise. */

    private boolean checkInputSchemaContents() {
         boolean errorFlag = false;
         int colIndex=0;

         // Loop through top line of schema file
         for ( ;colIndex<schema[0].length;colIndex++) {
	    if (!schema[0][colIndex].equals("int") &&
	    		!schema[0][colIndex].equals("double") &&
	    		!schema[0][colIndex].equals("nominal") &&
	    		!schema[0][colIndex].equals("unused")) {
		errorFlag=true;
		break;
		}
	    }

	// Default
	if (errorFlag) {
	    JOptionPane.showMessageDialog(null,"ERROR: unrecognised first " +
	    	     "line schema label, found\n" + schema[0][colIndex] +
		     " when expecting \"int\", \"double\", or  \"nominal\"\n");
	    return(false);
	    }
	return(true);
	}

    /* CHECK NOMINAL DATA */
    /** Checks third line of schema matches second line of schema and
    vice versa.
    @return true if third line of schema matches second line, false otherwise. */

    private boolean checkNominalData() {
        boolean returnValue=true;

        // Loop through third line of schema data to make sure that
        // each nominal attribute has a value list associated with it.
         for (int colIndex=0;colIndex<schema[0].length;colIndex++) {
            if (schema[0][colIndex].equals("nominal") && nominalData[colIndex]==null) {
            	 JOptionPane.showMessageDialog(null,"ERROR: Attribute " + colIndex +
            	 		", nominal data items\nmust have a value " +
            	 			" list associated with them\n");
            	 returnValue=false;
            	 }
            }

        // Loop through nominal data structure to make sure that
        // each nominal attribute has a value list associated with it.
        for (int colIndex=0;colIndex<nominalData.length;colIndex++) {
            if (nominalData[colIndex]!=null && !schema[0][colIndex].equals("nominal"))
                JOptionPane.showMessageDialog(null,"WARNING: Attribute " + colIndex +
            	 		", list of values supplied\nbut type is " +
            	 		schema[0][colIndex] + "\n");
            }

        // Return
        return(returnValue);
        }

    /* ----------------------------------------------------- */
    /*                                                       */
    /*                     INPUT MAX RANGE                   */
    /*                                                       */
    /* ----------------------------------------------------- */

    /* INPUT MAX RANGE */
    /** Commences process of inputting max range. */
    private void inputNumRanges() {
        textArea.append("INPUT MAXIMUMRANGE VALUE:\n-------------------------\n");

        try{
           while (true) {
               String stNum1 = JOptionPane.showInputDialog(
               		"Input maximum number of ranges. Should be a positive\n" +
               		"integer\n\n" );
	       maxRange = Integer.parseInt(stNum1);
               if (maxRange > 0) break;
               JOptionPane.showMessageDialog(null,
	       		"MAXIMUM NUMBER OF RANGES VALUE INPUT ERROR:\n" +
	       		"input = " + maxRange +
	       		"\nmaximum number of ranges input must\n" +
	       		"be an integer greater than zero\n");
	       }
	    textArea.append("Maximum number of ranges = " + maxRange + "\n\n");
	    hasMaxRangeFlag=true;
	    }
        catch(NumberFormatException e) {
	    }
	}

    /* ---------------------------------------------- */
    /*                                                */
    /*                       EDIT PANEL               */
    /*                                                */
    /* ---------------------------------------------- */

    /* MOVE COLUMN N TO END */
    /** Moves a column in the input file to the end of the input data (user
    prompted for column number to be moved). */
    private void moveColNtoEnd() {
        int newColIndex;
        // Input column number to be removed
        textArea.append("MOVE COLUMN TO END:\n--------------------\n");
        int colNumToBeMovedToEnd = inputColNumber("Input column number to be moved");
        textArea.append("Column number to be moved to end = " + (colNumToBeMovedToEnd+1) + "\n");

	// Move
        moveColInInputData(colNumToBeMovedToEnd,numColsInInputData);
        moveColInInputSchema(colNumToBeMovedToEnd,numColsInInputData);
        moveColInNormalisationData(colNumToBeMovedToEnd,numColsInInputData);

        // Calculate new min and max values for ranges, and number of missing items
	determinMaxMimDataArray();
	recalculateMissingItems();

        // End
        textArea.append("Column number " + (colNumToBeMovedToEnd+1) + " moved to end\n\n");
        }

    /* MOVE COLUMN N TO BEFORE COLUMN M */
    /** Moves a column in the input file to another position in the input data (user
    prompted for column number to be moved and column number to be moved to). */
    private void moveColNtoBeforeColM() {
        int newColIndex;

        // Input column number to be removed
        textArea.append("MOVE COLUMN:\n-------------\n");
        int colNumToBeMoved = inputColNumber("Input column number to be moved");
        int toColumnNumber  = inputColNumber("Input column number for colum " +
        		(colNumToBeMoved+1) + " to be moved to");
        textArea.append("Column number " + (colNumToBeMoved+1) + " to be moved to " +
        		(toColumnNumber+1) + "\n");

        // Move
        int toNumber=toColumnNumber;
        if (toColumnNumber>colNumToBeMoved) toNumber--;
        moveColInInputData(colNumToBeMoved,toNumber);
        moveColInInputSchema(colNumToBeMoved,toNumber);
        moveColInNormalisationData(colNumToBeMoved,toNumber);

        // Calculate new min and max values for ranges, and number of missing items
	determinMaxMimDataArray();
	recalculateMissingItems();

        // End
        textArea.append("Column number " + (colNumToBeMoved+1) + " moved to " +
        		(toColumnNumber+1) + "\n\n");
        }

    /* MOVE COLUMN IN INPUT DATA */
    /** Moves column in input data structure.
    @param colToBeMoved The column number identifier for the column to be moved.
    @param toColumnNumber The column number identifier for the column before which
    the column to be moved is tombe inswerted.  */

    private void moveColInInputData(int colToBeMoved, int toColumnNumber) {
        int localNumRows = inputDataArray.length;

        // Store input for column to be moved}
        double[] inputToBeMoved = new double[localNumRows];
        for (int rowIndex=0;rowIndex<localNumRows;rowIndex++)
        	inputToBeMoved[rowIndex]=inputDataArray[rowIndex][colToBeMoved];

        // Remove column to be moved from input data structure
        removeColNfromInput(colToBeMoved);

        // Increment input data column counter, and create new input data structure
        numColsInInputData++;
        double[][] newInputDataArray = new double[localNumRows][numColsInInputData];

        // Loop through current data and create new data structure
        for (int rowIndex=0;rowIndex<localNumRows;rowIndex++) {
            int newColIndex=0;
            boolean columnMoved=false;
            // Loop through row column ny column
            for (int colIndex=0;colIndex<inputDataArray[rowIndex].length;colIndex++) {
	        if (colIndex==toColumnNumber) {
		    newInputDataArray[rowIndex][newColIndex] =
		    					inputToBeMoved[rowIndex];
	            newColIndex++;
	            columnMoved=true;
	            }
	        newInputDataArray[rowIndex][newColIndex] =
	        					inputDataArray[rowIndex][colIndex];
	        newColIndex++;
	        }
	    // Add to end
	    if (!columnMoved) newInputDataArray[rowIndex][newColIndex] =
	    						inputToBeMoved[rowIndex];
	    }

	// Assign new data structure identifier to current data field
	inputDataArray = newInputDataArray;
        }

    /* MOVE COLUMN IN SCHEMA */
    /** Moves column in schema structure.
    @param colToBeMoved The column number identifier for the column to be moved.
    @param toColumnNumber The column number identifier for the column before which
    the column to be moved is tombe inswerted.  */

    private void moveColInInputSchema(int colToBeMoved, int toColumnNumber) {
        // Store schema attributes for column to be moved
        String attributeToBeMoved0 = schema[0][colToBeMoved];
        String attributeToBeMoved1 = schema[1][colToBeMoved];

        // Remove column to be moved from schema data structure
        removeColNfromInputSchema(colToBeMoved);

        // Increment number of columns in schema, and create new schema data
        // structure
        numColsInInputSchema++;
        String[][] newInputSchema  = new String[2][numColsInInputSchema];

	// Loop through current schema and create new scheam data srtructure
        int newColIndex=0;
	boolean columnMoved=false;
        for (int colIndex=0;colIndex<(numColsInInputSchema-1);colIndex++) {
            if (colIndex==toColumnNumber) {
	        newInputSchema[0][newColIndex] = attributeToBeMoved0;
	        newInputSchema[1][newColIndex] = attributeToBeMoved1;
	        newColIndex++;
	        newInputSchema[0][newColIndex] = schema[0][colIndex];
	        newInputSchema[1][newColIndex] = schema[1][colIndex];
	        columnMoved=true;
	        }
	    else {
	        newInputSchema[0][newColIndex] = schema[0][colIndex];
	        newInputSchema[1][newColIndex] = schema[1][colIndex];
	        }
	    newColIndex++;
	    }

	// Add to end?
	if (!columnMoved) {
	    newInputSchema[0][newColIndex] = attributeToBeMoved0;
	    newInputSchema[1][newColIndex] = attributeToBeMoved1;
	    }

	// Assign new schema structure identifier to current schema field
	schema = newInputSchema;
	}

    /* MOVE COLUMN IN NORMALISATION DATA */
    /** Moves column in normalisation data structure.
    @param colToBeMoved The column number identifier for the column to be moved.
    @param toColumnNumber The column number identifier for the column before which
    the column to be moved is tombe inswerted.  */

    private void moveColInNormalisationData(int colToBeMoved, int toColumnNumber) {
        // Store normalisation data for column to be moved
        String[] nominalDataToBeMoved =
        			stringArrayCopy(nominalData[colToBeMoved]);

        // Remove column to be moved from normalisation data structure
        removeColNfromNormalisationData(colToBeMoved);

        // Increment number of columns in normalisation data, and create new
        // nominal data structure
        numColsInNormalisationData++;
        String[][] newNominalData  = new String[numColsInNormalisationData][];

        // Insert into new normalisation structure
        int newColIndex=0;
	boolean columnMoved=false;
	for (int colIndex=0;colIndex<(numColsInNormalisationData-1);colIndex++) {
	    if (colIndex==toColumnNumber) {
	        newNominalData[newColIndex] = stringArrayCopy(nominalDataToBeMoved);
	        newColIndex++;
	        newNominalData[newColIndex] = stringArrayCopy(nominalData[colIndex]);
	        columnMoved=true;
	        }
	    else newNominalData[newColIndex] = stringArrayCopy(nominalData[colIndex]);
	    newColIndex++;
	    }

	// Add to end?
	if (!columnMoved) newNominalData[newColIndex] =
						stringArrayCopy(nominalDataToBeMoved);

	// Assign new schema file identifier to current data file field
	nominalData = newNominalData;
        }

    /* STRING ARRAY COPY */
    /** Copies contents of given array of strings to a second array.
    @param fromStringArray the given string array which is to be copied.
    @return copy of the given string. */

    private String[] stringArrayCopy(String[] fromStringArray) {
        // If from string is null set to string to null and return
        if (fromStringArray==null) return(null);

        // Dimension to string array
        String[] toStringArray = new String[fromStringArray.length];

        // Loop through from string array
        for (int index=0;index<fromStringArray.length;index++)
            			toStringArray[index]=fromStringArray[index];

        // Return
        return(toStringArray);
        }

    /* REMOVE COLUMN N */
    /** Removes a column from the input file (user prompted for column number to be
    moved). */
    private void removeColN() {
        // Input column number to be removed
        textArea.append("REMOVED COLUMN:\n-------------------\n");
        int colNumToBeRemoved = inputColNumber("Input column number to be removed");
        textArea.append("Column number to be removed = " + (colNumToBeRemoved+1) + "\n");

        // Are you sure?
        if (confirmColRemoval(colNumToBeRemoved)) {
            // Remoce column from input data structure
            removeColNfromInput(colNumToBeRemoved);
            // Remove column from schema structure
            removeColNfromInputSchema(colNumToBeRemoved);
	    // Remove column from nornmalisation data structure
            removeColNfromNormalisationData(colNumToBeRemoved);
	    // Calculate new min and max values for ranges, and number of missing items
	    determinMaxMimDataArray();
	    recalculateMissingItems();
	    // End
	    textArea.append("Column number " + (colNumToBeRemoved+1) + " removed\n\n");
	    textArea.append("Number of records = " + numRows +
	                      "\nNumber of columns = " + numColsInInputData +
	                      "\nNum. missing values = " + numMissingItems +
	                      "\n\n");
	    }
        }

    /* REMOVE COLUMN FROM INPUT  */
    /** Removes given column number from input data structure.
    @param colNumToBeRemoved The column identifier of the column to be removed
    from the input data structure. */

    private void removeColNfromInput(int colNumToBeRemoved) {
        // Decrement number of columns in data
        numColsInInputData--;

        // Create new data structure
        double[][] newInputDataArray  = new double[numRows][numColsInInputData];

        // Loop through current data and create new data structure
        for (int rowIndex=0;rowIndex<inputDataArray.length;rowIndex++) {
            for (int colIndex=0, newColIndex=0;colIndex<inputDataArray[rowIndex].length;
                					colIndex++) {
	        if (colIndex!=colNumToBeRemoved) {
	            newInputDataArray[rowIndex][newColIndex] =
	                				inputDataArray[rowIndex][colIndex];
	            newColIndex++;
	            }
	        }
	    }

	// Assign new data structure identifier to current data field
	inputDataArray = newInputDataArray;
	}

    /* REMOVE COLUMN FROM SCHEMA */
    /** Removes given column number from schema data structure.
    @param colNumToBeRemoved The column identifier of the column to be removed
    from the schema structure. */

    private void removeColNfromInputSchema(int colNumToBeRemoved) {
        // Decrement number of columns in schema
        numColsInInputSchema--;

        // Create new schema file
        String[][] newInputSchema  = new String[2][numColsInInputSchema];

        // Loop through current schema and create new schema data structure
        for (int colIndex=0, newColIndex=0;colIndex<=numColsInInputSchema;
                					colIndex++) {
	    if (colIndex!=colNumToBeRemoved) {
	        newInputSchema[0][newColIndex]=schema[0][colIndex];
	        newInputSchema[1][newColIndex]=schema[1][colIndex];
	        newColIndex++;
	        }
	    }

	// Assign new schema structure identifier to current schema field
	schema = newInputSchema;
	}

    /* REMOVE COLUMN FROM NORMALISATION DATA */
    /** Removes given column number from normalisation data structure.
    @param colNumToBeRemoved The column identifier of the column to be removed
    from the normalisation data structure. */

    private void removeColNfromNormalisationData(int colNumToBeRemoved) {
        // Decrement number of columns in normalisation data
        numColsInNormalisationData--;

        // Create new nominal data structure
        String[][] newNominalData  = new String[numColsInNormalisationData][];

        // Loop through current nominal data and create new nominal data structure
        for (int colIndex=0, newColIndex=0;colIndex<=numColsInNormalisationData;
                					colIndex++) {
	    if (colIndex!=colNumToBeRemoved) {
	        newNominalData[newColIndex] = nominalData[colIndex];
	        newColIndex++;
	        }
	    }

	// Assign new schema file identifier to current data file field
	nominalData = newNominalData;
	}

    /* INPUT COLUMN NUMBER TO BE REMOVED */
    /** Creates message window into which user can enter a column number (used in
    conjunction with edit methods such as <TT>moveColNtoEnd</TT>,
    <TT>moveColNtoBeforeColM</TT> and <TT>removeColN</TT>).
    @param s1 The input string to be output in message window
    @return the column number input by the user. */
    private int inputColNumber(String s1) {
        int colNumber=0;

        try{
           while (true) {
               String stNum1 = JOptionPane.showInputDialog(s1 +
	       		" (integer\nwithin the range 1 and " + numColsInInputData + ")\n\n");
	       colNumber = Integer.parseInt(stNum1);
               if (colNumber >= 1 && colNumber <= numColsInInputData) break;
               JOptionPane.showMessageDialog(null,
	       		"INVALID COLUMN NUMBER INPUT ERROR:\n" +
	       		"input = " + colNumber +
	       		"\ncolumn number number must be within the\n" +
	       		"range 1 and " + numColsInInputData + "\n\n");
	       }
	    }
        catch(NumberFormatException e) {
	    }

	// Return
	return(colNumber-1);
	}

    /* CONFIRM */
    /** Creates a message window to allow user to confirm a given column number
    (used with <TT>removeColN</TT> edit method).
    @param colNumber The column number to be confirmed
    @return true of OK, false otherwise. */
    private boolean confirmColRemoval(int  colNumber) {
        int result = JOptionPane.showConfirmDialog(null,
	       	"CONFIRM COLUMN NUMBER TO BE REMOVED:\nAre you sure you " +
	       	"wish to remove column " + (colNumber+1) + "\nfrom the " +
	       	"input data set? (THIS OPERATION IS\nNOT REVERSABLE!)\n\n",
	       	"Confirm",JOptionPane.YES_NO_OPTION);
        if (result==0) return(true);
        else return(false);
        }

    /* ----------------------------------------------- */
    /*                                                 */
    /*                     RANDOMIZE                   */
    /*                                                 */
    /* ----------------------------------------------- */

    /* RANDOMIZE ROWS */
    /** Produces a ranmdomised data file. */

    private void randomizeRows() {
	int numRecords = numRows;
        int randomIndex=0, newIndex=0;

        // Output
        textArea.append("RANDOMIZE INPUT DATA ROWS\n" +
					"----------------------------\n");

        // Create new data file
        double[][] newInputDataArray = new double[numRows][numColsInInputData];

    	while (numRecords>1) {
            randomIndex = (int) (Math.random()*(numRecords-1));
            for (int colIndex=0;colIndex<inputDataArray[randomIndex].length;
	    							colIndex++) {
                newInputDataArray[newIndex][colIndex] =
					inputDataArray[randomIndex][colIndex];
                }
            newIndex++;
            numRecords--;
            if (randomIndex!=numRecords) {
                inputDataArray[randomIndex] =
				new double[inputDataArray[numRecords].length];
                for (int colIndex=0;colIndex<inputDataArray[numRecords].length;
								colIndex++) {
                    inputDataArray[randomIndex][colIndex] =
		    			inputDataArray[numRecords][colIndex];
                    }
                }
            }

	// Last lines
        for (int colIndex=0;colIndex<inputDataArray[randomIndex].length;
								colIndex++) {
            newInputDataArray[newIndex][colIndex] =
	    			        inputDataArray[randomIndex][colIndex];
            }

        // Assign new data file identifier to current data file field
	inputDataArray = newInputDataArray;
	// End
	textArea.append("Randomization complete\n\n");
	}

    /* ---------------------------------------------- */
    /*                                                */
    /*                    NORMALISE                   */
    /*                                                */
    /* ---------------------------------------------- */

    /* NORMALISE */
    /** Commences normalisation process. */

    private void normalise() {
        textArea.append("NORMALISE DATA\n----------------\n");

        // Process
	determineBinaryOutput();

	// End
	hasNormalisationFlag = true;
	textArea.append("Normalisation complete\n\n");
	}

    /* DETERMINE BINARY OUTPUT */
    /** Commence process of identifying binary output. */

    private void determineBinaryOutput() {
        int attributeNumber=1;

        // Dimension output data array
        outputDataArray  = new int[numRows][numColsInInputData];

	// Process rows
        for (int rowIndex=0;rowIndex<inputDataArray.length;rowIndex++) {
	    attributeNumber=1;
	    // process columns in row
	    for (int colIndex=0;colIndex<inputDataArray[rowIndex].length;
	    							colIndex++) {
		attributeNumber = determineBinaryOutput(attributeNumber,
				inputDataArray[rowIndex][colIndex],rowIndex,
								colIndex);
		}
	    }

	numAttributes=attributeNumber-1;
        }

    /* DETERMINE BINARY OUTPUT */
    /** Determines whether value is an integer, double or boolean.
    @param attNum the current attribute (column) number.
    @param number the given value in the current row/column intersection in
        the input data structure.
    @param rowIndex the current row index in the input data structure.
    @param colIndex the current column index in the input data structure.
    @return the revised attribute number. */

    private int determineBinaryOutput(int attNum, double number,
    						int rowIndex, int colIndex) {

        // Integer
	if (schema[0][colIndex].equals("int"))
			return(determineBinaryOutputInt(attNum,
			number,(int) minMaxData[0][colIndex],
			minMaxData[2][colIndex],rowIndex,colIndex));

	// Double
	if (schema[0][colIndex].equals("double"))
			return(determineBinaryOutputDouble(attNum,
			number,minMaxData[0][colIndex],
			minMaxData[2][colIndex],rowIndex,colIndex));

	// Boolean
	if (schema[0][colIndex].equals("nominal"))
			return(determineBinaryOutputNominal(attNum,
			number,(int) minMaxData[0][colIndex],
			minMaxData[2][colIndex],rowIndex,colIndex));

	// Default (i.e. ignore)
	return(attNum);
	}

    /* DETERMINE BINARY OUTPUT NOMINAL (VALUE) */
    /** Output binary equivalent of nominal value. <P> Example attNum = 6,
    number 5, startRange = 4, range = 3; the output will be
    attNum+number-startTange = 6+5-4=7. The return value will be
    attNum+range+1 = 5+3+1=9
    @param attNum the current attribute (column) number.
    @param number the given value in the current row/column intersection in the
    	input data structure.
    @param startRange the numeric value marking the start of the range for the
    	input column.
    @param range the range value (for the input column)
    @param rowIndex the current row index in the input data structure.
    @param colIndex the current column index in the input data structure.
    @return the revised attribute number.  */

    private int determineBinaryOutputNominal(int attNum, double number,
    			int startRange, double range, int rowIndex,
			int colIndex) {
        // Test for missing value
        if (number<TEST_TERM) outputDataArray[rowIndex][colIndex] =
							(int) MISSING_ITEM;
	// Otherwise process
	else outputDataArray[rowIndex][colIndex] = attNum + (int) number -
							startRange;
	return(attNum+(int) range + 1);
	}

    /* DETERMINE BINARY OUTPUT INTEGER (VALUE) */
    /** Output binary equivalent of integer value. <P> Two possibilities: (i)
    integer range is such that it will fit into the given range in which case
    use as many attribute numbers as necessary, or (ii) integer range does not
    fit into given range in which case apply a scaling so that it does fit into
    the maximum range.
    @param attNum the current attribute (column) number.
    @param number the given value in the current row/column intersection in the
    	input data structure.
    @param startRange the numeric value marking the start of the range for the
    	input column.
    @param range the range value (for the input column)
    @param rowIndex the current row index in the input data structure.
    @param colIndex the current column index in the input data structure.
    @return the revised attribute number.  */

    private int determineBinaryOutputInt(int attNum, double number,
    					int startRange, double range,
					int rowIndex, int colIndex) {
	// Output where number is an integer and range for column is less
	// than maximum, i.e. the integer will not fit into the given range.
	// Uses the identity:
	//   +-----------------------------------------------------------+
	//   |   newNumber = 1 + (maxRange*(number-startRange)/range)    |
	//   +-----------------------------------------------------------+
  	if (range > maxRange) {
	    // Missing value
	    if (number<TEST_TERM)
	    	outputDataArray[rowIndex][colIndex] = (int) MISSING_ITEM;
	    else {
	        // Determine value out of range of maxRange
	        int newNumber = (int)
	    			((double) maxRange*(number-startRange)/range);
	        // Test and adjust for special case.
	        if (newNumber==maxRange) newNumber=maxRange-1;
	        // Output
	        outputDataArray[rowIndex][colIndex] = attNum+newNumber;
		}
	    return(attNum+maxRange);
	    }
	// Output where number is an integer and range for column is less than
	// maximum (i.e. it will fit). Example attNum = 6, number 5,
	// startRange = 4; the output will be 6+5-4 = 7. The return value will
	// = attNum+range+1
	else {
	    if (number<TEST_TERM) outputDataArray[rowIndex][colIndex] =
	    					(int) MISSING_ITEM;
	    else outputDataArray[rowIndex][colIndex] = attNum +
	    					(int) number - startRange;
	    return(attNum+(int) range + 1);
	    }
	}

    /* DETERMINE BINARY OUTPUT DOUBLE (VALUE) */
    /** Output binary equivalent of double value. <P>Uses the identity:<PRE>
	+-----------------------------------------------------------+
	|   newNumber = 1 + (maxRange*(number-startRange)/range)   |
	+-----------------------------------------------------------+
    </PRE>
    @param attNum the current attribute (column) number.
    @param number the given value in the current row/column intersection in the
    	input data structure.
    @param startRange the numeric value marking the start of the range for the
    	input column.
    @param range the range value (for the input column)
    @param rowIndex the current row index in the input data structure.
    @param colIndex the current column index in the input data structure.
    @return the revised attribute number.*/

    private int determineBinaryOutputDouble(int attNum, double number,
    					double startRange, double range,
					int rowIndex, int colIndex ) {

        if (range > 0.0) {
            // Test for missing value
	    if (number<TEST_TERM) outputDataArray[rowIndex][colIndex] =
			              (int) MISSING_ITEM;
	    else {
	        // Determine value out of range of maxRange
	        int newNumber = (int) ((double) maxRange*(number-startRange)/range);
                // Test and adjust for special case.
	        if (newNumber==maxRange) newNumber=maxRange-1;
	        // Output
	        outputDataArray[rowIndex][colIndex] = attNum + newNumber;
	        }
	    // Return
	    return(attNum+maxRange);
            }
	// range less than or equal to 0.0
	else {
	    // Test for missing value
	    if (number<TEST_TERM) outputDataArray[rowIndex][colIndex] =
			             (int) MISSING_ITEM;
	    else outputDataArray[rowIndex][colIndex] = attNum;
	    // Return
	    return(attNum+1);
            }
	}

    /* ------------------------------------------------- */
    /*                                                   */
    /*                   OUTPUT METHODS                  */
    /*                                                   */
    /* ------------------------------------------------- */

    /* LIST INPUT DATA */
    /** Outputs stored input data set; initially read from input data file. */

    private void listInputData() {
        textArea.append("LIST DATA\n--------\n");

        for(int index=0;index<inputDataArray.length;index++) {
	    outputItemSet(inputDataArray[index]);
	    textArea.append("\n");
	    }

	// End
	textArea.append("\n");
	}

    /* OUTPUT ITEMSET */
    /** Outputs a given item set of doubles (outputs a "?" if a missing value is
    detected.
    @param itemSet the given item set. */

    private void outputItemSet(double[] itemSet) {
	String itemSetStr = null;

	// If empty item set return
	if (itemSet==null) return;

	// Output first item
	if (itemSet[0]<TEST_TERM) itemSetStr = "?";
	else itemSetStr = Double.toString(itemSet[0]);

	// Output rest
	for (int index=1;index<itemSet.length;index++) {
	    if (itemSet[index]<TEST_TERM) itemSetStr = itemSetStr + " ?";
	    else itemSetStr = itemSetStr + " " + itemSet[index];
	    }

	// Output
	textArea.append(itemSetStr);
	}

    /** Outputs a given item set of integers (omits missing values).
    @param itemSet the given item set. */
    private void outputItemSet(int[] itemSet) {
	String itemSetStr = null;
	boolean noItemsInItemSetStr = true;

	// If empty item set return
	if (itemSet==null) return;

	// Output rest
	for (int index=0;index<itemSet.length;index++) {
	    if (itemSet[index]>TEST_TERM) {
	    	if (noItemsInItemSetStr) {
		    itemSetStr = Integer.toString(itemSet[index]);
		    noItemsInItemSetStr = false;
		    }
		else itemSetStr = itemSetStr + " " + itemSet[index];
		}
	    }

	// Output
	textArea.append(itemSetStr);
	}

    /* LIST OUTPUT DATA */
    /** Outputs stored normalised output data set. */

    private void listOutputData() {
        textArea.append("LIST OUTPUT DATA\n--------\n");

        for(int index=0;index<outputDataArray.length;index++) {
	    outputItemSet(outputDataArray[index]);
	    textArea.append("\n");
	    }

	// End
	textArea.append("\n");
	}

    /* LIST SCHEMA */
    /** Outputs stored schema for input data set. */

    private void listInputSchema() {
        textArea.append("LIST SCHEMA\n------------\n");

	// Output schema
	int number=1;
	for (int colIndex=0;colIndex<schema[0].length;colIndex++) {
	    textArea.append("(" + number + ") " + schema[0][colIndex] + ": ");
	    if (schema[0][colIndex].equals("nominal")) {
	        textArea.append(schema[1][colIndex] + " { ");
	        for (int index=0;index<nominalData[colIndex].length;index++)
	        		textArea.append(nominalData[colIndex][index] +
				" ");
	        textArea.append("}\n");
	        }
	    else textArea.append(schema[1][colIndex] + "\n");
	    number++;
	    }

	// End
	textArea.append("\n\n");
	}

    /* LIST MIN MAXDATA */
    /** Lists information in Min/Max 2D data array. */

    private void listMinMaxData() {
        textArea.append("LIST MIN-MAX DATA\n---------\n");

        // Loop through array
	for (int colIndex=0;colIndex<minMaxData[0].length;colIndex++) {
	    textArea.append("Range col. " + (colIndex+1) + ": " +
	    	   minMaxData[0][colIndex] + " to " + minMaxData[1][colIndex] +
		   " (range of " + minMaxData[2][colIndex] +")\n");
	    }

	// End
	textArea.append("\n");
	}

    /* LIST PARAMETERS */
    /** Ouyputs bumber of rows, colu,mns nad attributes to text area. */

    private void listParameters() {
        textArea.append("LIST PARAMETERS\n---------------\n");
        textArea.append("Number of records             = " + numRows + "\n");
	textArea.append("Number of cols (input data) = " + numColsInInputData +
			"\n");
	textArea.append("Number of cols (schema)     = " + numColsInInputSchema +
			"\n");
	textArea.append("Num. missing values           = " + numMissingItems +
			"\n");
	textArea.append("Max num. ranges              = " + maxRange +
			"\n");
	textArea.append("Number of attributess         = " + numAttributes +
			"\n");
	textArea.append("Density (%)                          = " +
					(twoDecPlaces(getDensity())) + "\n");

        // End output
        textArea.append("\n");
        }

    /* GET DENSITY */
    /* Calculates and returns the density of the normalised data set.
    @return the number of elements. */

    private double getDensity() {
        double counter=0.0;

        // Loop through output array
        for (int index=0;index<outputDataArray.length;index++) {
            counter = counter + (double) outputDataArray[index].length;
            }

        // Return
        double numberOfSlots = (double) (numRows*numAttributes);
        return((counter*100.0)/numberOfSlots);
        }

    /* TWO DECIMAL PLACES */

    /** Converts given real number to real number rounded up to two decimal
    places.
    @param number the given number.
    @return the number to two decimal places. */

    protected double twoDecPlaces(double number) {
    	int numInt = (int) ((number+0.005)*100.0);
	number = ((double) numInt)/100.0;
	return(number);
	}

    /* ----------------------------------------------------- */
    /*                                                       */
    /*			OUTPUT OUTPUT SCHEMA                 */
    /*                                                       */
    /* ----------------------------------------------------- */

    /* OUTPUT OUTPUT SCHEMA */
    /** Commences process of outputting output schema to textArea. */

    private void outputOutputSchema() {
        // Strat
        textArea.append("LIST OUTPUT SCHEMA\n---------------------\n");
        listOutputSchema2();

	// End
	textArea.append("\n");
        }

    /** Continues process of listing output schema (attribute labels) to
    textArea. <P> Done by processing schema array. */

    private void listOutputSchema2() {
	int attNum=1;

        for(int colIndex=0;colIndex<schema[0].length;colIndex++) {
	    // Integer
 	    if (schema[0][colIndex].equals("int"))
	        attNum = outputIntLabel(attNum,schema[1][colIndex],
	    	      			(int) minMaxData[0][colIndex],
		      			(int) minMaxData[1][colIndex],
					(int) minMaxData[2][colIndex]);
	    // Double
	    if (schema[0][colIndex].equals("double")) attNum =
	   		outputDoubleLabel(attNum,schema[1][colIndex],
	    	      	    minMaxData[0][colIndex],minMaxData[2][colIndex]);
	    // Nominal
	    if (schema[0][colIndex].equals("nominal"))
	        attNum = outputNominalLabel(attNum,schema[1][colIndex],
						colIndex);
	    }

	// End
	textArea.append("\n");
        }

    /* OUTPUT INTEGER LABEl */
    /** Outputs the attribute numbers and labels for an integer column.
    @param attNum The current attribute number.
    @param label The label to be output.
    @param startRange The start range for the column.
    @param endRange The end range for the column.
    @param range the range for the integer attribute (hoe many values).
    @return the new current attribute number. */

    private int outputIntLabel(int attNum, String label, int startRange,
    			int endRange, int range) {
        // Output where number is an integer and range for column is greater
	// than maximum.
  	if (range > maxRange) {
  	    double oldMarker = 0.0;
	    for (int index=1;index<=maxRange;index++) {
	    	double marker = startRange +
				(((double) (index*range))/maxRange);
	        if (index==1) {
	            outputLabel(attNum,label+"<"+marker);
	            oldMarker = marker;
	            }
	        else if (index==maxRange) outputLabel(attNum,label+">="+oldMarker);
	        else {
	            outputLabel(attNum,oldMarker + "<=" +label+"<"+marker);
	            oldMarker = marker;
	            }
	        attNum++;
	        }
	    // Return
	    return(attNum);
	    }

	// Output where number is an integer and range for column is less than
        // maximum.
        else {
            for (int index=startRange;index<=endRange;index++) {
                outputLabel(attNum,label+"="+index);
                attNum++;
                }
            // Return
            return(attNum);
	    }
	}

    /* OUTPUT DOUBLES LABEL */
    /** Outputs the attribute numbers and labels for an double column.
    @param attNum The current attribute number.
    @param label The label to be output.
    @param startRange The start range for the column.
    @param range the range for the integer attribute (hoe many values).
    @return the new current attribute number. */

    private int outputDoubleLabel(int attNum, String label,
    					double startRange, double range) {
        if (range > 0.00) {
            for (int index=1;index<=maxRange;index++) {
	        double marker = startRange +
				(((double) index*range)/(double) maxRange);
	        outputLabel(attNum,label+"<"+marker);
	        attNum++;
	        }
	    }
        else {
	    outputLabel(attNum,label);   
	    attNum++;
	    }

	// Return
	return(attNum);
	}

    /* OUTPUT NOMINAL LABEL */
    /** Produces label for nominal field.
    @param attNum The current attribute number.
    @param label The label to be output.
    @param colIndex the index in the nominal data structure.
    @return the new current attribute number. */

    private int outputNominalLabel(int attNum, String label, int colIndex) {
        for (int index=0;index<nominalData[colIndex].length;index++) {
	    outputLabel(attNum,label + "=" + nominalData[colIndex][index]);
	    attNum++;
	    }

	// Return
	return(attNum);
        }

    /* OUTPUT LABEL */
    /** Outputs label to text area.
    @param attNum The current attribute number.
    @param label The label to be output. */

    private void outputLabel(int attNum, String label) {
        if (outputSchemaFlag) fileOutput.println(label);
        else textArea.append("(" + attNum + ") " + label + "\n");
        }

    /* ------------------------------------------------------- */
    /*                                                         */
    /*                     SAVE NORMALISATION                  */
    /*                                                         */
    /* ------------------------------------------------------- */

    /* SAVE NORMALISATION */
    /** Outputs result of normalisation to file. */

    private void saveNormalisation() {
        textArea.append("SAVE NORMALIZATION\n--------------------\n");

        if (openOutputFile()) {
	    try {
   		outputToFile();
                }
            catch (IOException ioException) {
	    	JOptionPane.showMessageDialog(this,"Error Writing to File",
			 "Error",JOptionPane.ERROR_MESSAGE);
		}
            }
	}

    /* OUTPUT TO FILE */
    /** Commences process of outputting to file the normalised data. */

    private void outputToFile() throws IOException {
        // Initial output
	textArea.append("File name = " + outputFileName + "\n");

	// Test if data exists
	if (outputDataArray != null) {
  	    // Step through records
	    for (int index1=0;index1<outputDataArray.length;index1++) {
	    	// Write integers in record up to last integer
	    	int index2=0;
	    	for (;index2<outputDataArray[index1].length-1;index2++) {
		    int number = outputDataArray[index1][index2];
	            if (number>TEST_TERM) fileOutput.print(number + " ");
	            }
	    	// Write last integer
		int number = outputDataArray[index1][index2];
	        if (number>TEST_TERM) fileOutput.println(number);
                else fileOutput.println();
		}
            textArea.append("File output complete\n\n");
	    }
        else textArea.append("No data\n\n");

	// End by closing file
	fileOutput.close();
	}

    /* OPEN OUTPUT FILE */
    /** Opens the output file ready to write normalised data to.
    @return true if file successfully opened, and false otherwise. */

    private boolean openOutputFile() {
	// Display file dialog box
	JFileChooser fileChooser = new JFileChooser();
	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	int result = fileChooser.showSaveDialog(this);

	// If cancel button selected return
	if (result == JFileChooser.CANCEL_OPTION) return(false);

	// Obtain selected file
	outputFileName = fileChooser.getSelectedFile();

	// Dispaly error if invalid
	if (outputFileName ==  null || outputFileName.getName().equals("")) {
	    JOptionPane.showMessageDialog(this,"Invalid File name",
			"Invalid File name.",JOptionPane.ERROR_MESSAGE);
	    return(false);
	    }
	else {
   	    try {
		fileOutput = new PrintWriter(new FileWriter(outputFileName));
	 	return(true);
		}
	    catch(IOException ioException) {
	    	JOptionPane.showMessageDialog(this,"Error opening File",
			 "Error",JOptionPane.ERROR_MESSAGE);
      		return(false);
		}
	    }
	}

    /* ------------------------------------------------------- */
    /*                                                         */
    /*                     SAVE OUTPUT SCHEMA                  */
    /*                                                         */
    /* ------------------------------------------------------- */

    /* SAVE OUTOUT SCHEMA */
    /** Saves output schema (attribute labels) to file. */

    private void saveOutputSchema() {
        textArea.append("SAVE OUTPUT SCHEMA\n--------------------\n");

        if (openOutputFile()) {
	    try {
   		outputSchemaToFile();
                }
            catch (IOException ioException) {
	    	JOptionPane.showMessageDialog(this,"Error Writing to File",
			 "Error",JOptionPane.ERROR_MESSAGE);
		}
            }
	}

    /* OUTPUT SCHEMA TO FILE */
    /** Commences process of outputting to the output schema to file. */

    private void outputSchemaToFile() throws IOException {
        // Initial output
	textArea.append("File name = " + outputFileName + "\n");

        // Output
	outputSchemaFlag = true;
	listOutputSchema2();
	outputSchemaFlag = false;

	// End by closing file
	fileOutput.close();
	}
	
    /* ------------------------------------------------------- */
    /*                                                         */
    /*                  FILE HANDLING UTILITIES                */
    /*                                                         */
    /* ------------------------------------------------------- */
	
    /* OPEN INPUT FILE */
    /** Opens input file. */
    private void openInputFile() {
	try {
	    // Open file
	    FileReader file = new FileReader(inputFileName);
	    fileInput = new BufferedReader(file);
	    }
	catch(IOException ioException) {
	    JOptionPane.showMessageDialog(this,"Error Opening File",
			 "Error 4: ",JOptionPane.ERROR_MESSAGE);
	    }
	}
	
    /* CLOSE INPUT FILE */
    /* Closes input file. */
    private void closeInputFile() {
        if (fileInput != null) {
	    try {
	    	fileInput.close();
		}
	    catch (IOException ioException) {
	        JOptionPane.showMessageDialog(this,"Error Opening File",
			 "Error 4: ",JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}
	
    /* CHECK FILE NAME */	
    /** Checks nature of input file name selected by user.
    @return false if selected file is a directory, access is denied or is
    not a file name and true otherwise. */
	
    private boolean checkFileName() {
	if (inputFileName.exists()) {
	    if (inputFileName.canRead()) {
		if (inputFileName.isFile()) return(true);
		else JOptionPane.showMessageDialog(null,
				"FILE ERROR: File is a directory");
		}
	    else JOptionPane.showMessageDialog(null,
	    			"FILE ERROR: Access denied");
	    }
	else JOptionPane.showMessageDialog(null,
				"FILE ERROR: No such file!");
	// Return
	
	return(false);
	}
	
    /* ------------------------------------------------------- */
    /*                                                         */
    /*                       INSTRUCTIONS                      */
    /*                                                         */
    /* ------------------------------------------------------- */

    /* INSTRUCTIONS */
    /** Outputs instructions to window. */

    private void instructions() {
        textArea.append("\nNOTE: Before data can be nornmalised user must:\n" +
        	"(1) load schema file,\n" +
        	"(2) load data file --- either space or comma separated, " +
        	"and\n(3) set number of ranges value\n\n");
        }

    /* ------------------------------------------------------- */
    /*                                                         */
    /*                       ARM UTILITIES                     */
    /*                                                         */
    /* ------------------------------------------------------- */	

    /* REALLOC 1 */
    
    /** Resizes given item set so that its length is increased by one
    and append new element
    @param oldItemSet the original item set
    @param newElement the new element/attribute to be appended
    @return the combined item set */
    
    protected double[] realloc1(double[] oldItemSet, double newElement) {
        
	// No old item set
	
	if (oldItemSet == null) {
	    double[] newItemSet = {newElement};
	    return(newItemSet);
	    }
	
	// Otherwise create new item set with length one greater than old 
	// item set
	
	int oldItemSetLength = oldItemSet.length;
	double[] newItemSet = new double[oldItemSetLength+1];
	
	// Loop
	
	int index;
	for (index=0;index < oldItemSetLength;index++)
		newItemSet[index] = oldItemSet[index];
	newItemSet[index] = newElement;
	
	// Return new item set
	
	return(newItemSet);
	}
	
    /* ------------------------------------------------------- */
    /*                                                         */
    /*                         MAIN METHOD                     */
    /*                                                         */
    /* ------------------------------------------------------- */
    	
    /* MAIN METHOD */
    /* Main method. */
    public static void main(String[] args) throws IOException {
	// Create instance of class AprioriTgui
	LUCS_KDD_DN_ARM newFile = new LUCS_KDD_DN_ARM("Data Preparation GUI (ARM data)");
        
	// Make window vissible
	newFile.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	newFile.setSize(700,810);
        newFile.setVisible(true);
        }
    }
