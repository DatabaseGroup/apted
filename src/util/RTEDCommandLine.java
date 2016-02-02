//    Copyright (C) 2012  Mateusz Pawlik and Nikolaus Augsten
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Affero General Public License as
//    published by the Free Software Foundation, either version 3 of the
//    License, or (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Affero General Public License for more details.
//
//    You should have received a copy of the GNU Affero General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Vector;

import distance.RTED_InfoTree_Opt;

/**
 * This is the command line access for running RTED algorithm.
 * 
 * @author Mateusz Pawlik
 *
 */
public class RTEDCommandLine {
	
	private String helpMessage = 
    		"\n" +
    		"Compute the edit distance between two trees.\n" +
    		"\n" +
            "SYNTAX\n" +
    		"\n" +
    		"  Simple Syntax -- use default algorithm (RTED):\n" +
    		"\n" +
    		"    java -jar RTED.jar {-t TREE1 TREE2 | -f FILE1 FILE2} [-c CD CI CR] [-v] [-m]\n" +
    		"\n" +
    		"    java -jar RTED.jar -h\n" +
    		"\n" +
    		"  Advanced Syntax -- use other algorithms or user-defined strategies:\n" +
    		"\n" +
    		"    java -jar RTED.jar {-t TREE1 TREE2 | -f FILE1 FILE2} -s {left|right|heavy}\n" +
    		"        [-sw] [-c CD CI CR] [-v] [-m]\n" +
    		"    java -jar RTED.jar {-t TREE1 TREE2 | -f FILE1 FILE2} -a FILE\n" +
    		"        [-c CD CI CR] [-v] [-m]\n" +
    		"    java -jar RTED.jar {-t TREE1 TREE2 | -f FILE1 FILE2} \n" +
    		"        [-l | -r | -k | -d | -o] [-c CD CI CR] [-v] [-m]\n" +
    		"\n" +
    		"DESCRIPTION\n" +
    		"\n" +
    		"    Compute the edit distance between two trees. If not otherwise\n" +
    		"    specified, the RTED algorithm by Pawlik and Augsten [1] is\n" +
    		"    used. This algorithm uses the optimal path strategy.\n" +
    		"\n" +
    		"    Optionally, the tree edit distance can be computed using the\n" +
    		"    strategies by Zhang and Shasha [2], Klein [3], Demaine et al. [4],\n" +
    		"    or a combination of thereof. The trees are either specified on the\n" +
    		"    command line (-t) or read from files (-f). The default output only\n" +
    		"    prints the tree edit distance, the verbose output (-v) adds\n" +
    		"    additional information such as runtime and strategy statistics.\n" +
    		"\n" +
    		"    In additon to the tree edit distance, the minimal edit mapping between\n" +
    		"    two trees can be computed (-m). There might be multiple minimal edit\n" +
    		"    mappings. This option computes only one of them.\n" +
    		"\n" +
    		"OPTIONS\n" +
    		"\n" +
            "    -h, --help \n" +
            "        print this help message.\n" +
            "\n" +
            "    -t TREE1 TREE2,\n" +
            "    --trees TREE1 TREE2\n" +
            "        compute the tree edit distance between TREE1 and TREE2. The\n" +
            "        trees are encoded in the bracket notation, for example, in tree\n" +
            "        {A{B{X}{Y}{F}}{C}} the root node has label A and two children\n" +
            "        with labels B and C. B has three children with labels X, Y, F.\n" +
            "\n" +
            "    -f FILE1 FILE2, \n" +
            "    --files FILE1 FILE2\n" +
            "        compute the tree edit distance between the two trees stored in\n" +
            "        the files FILE1 and FILE2. The trees are encoded in bracket\n" +
            "        notation.\n" +
            "\n" +
            "    -c CD CI CR, \n" +
            "    --costs CD CI CR\n" +
            "        set custom cost for edit operations. Default is -c 1 1 1.\n" +
            "        CD - cost of node deletion\n" +
            "        CI - cost of node insertion\n" +
            "        CR - cost of node renaming\n" +
            "\n" +
            "    -s {left|right|heavy}, \n" +
            "    --strategy {left|right|heavy}\n" +
            "        set custom strategy that uses exclusively left, right, or\n" +
            "        heavy paths.\n" +
            "\n" +
            "    -w, --switch\n" +
            "        force to switch trees if the left-hand tree is smaller than\n" +
            "        the right-hand tree.\n" +
            "\n" +
            "    -a FILE\n" +
            "    --strategy-array FILE\n" +
            "        read the strategy from FILE. Rows in the file represent\n" +
      		"        subtrees of the left-hand tree in postorder, columns represent\n" +
			"        subtrees of the right-hand tree in postorder. Strategies are\n" +
			"        separated with space.  Use digits 0, 1, 2 for left, right, and\n" +
			"        heavy path in the left-hand tree and 4, 5, 6 for left, right,\n" +
			"        and heavy path in the right-hand tree. Example strategy for\n" +
			"        two trees with three nodes each:\n" +
            "            0 1 2\n" +
            "            4 5 6\n" +
            "            2 6 0\n" +
            "\n" +
            "    -v, --verbose\n" +
            "        print verbose output, including tree edit distance, runtime,\n" +
            "        number of relevant subproblems and strategy statistics.\n" +
            "\n" +
            "    -l, --ZhangShashaLeft\n" +
            "        like \"-s left\". Use the algorithm by Zhang and Shasha [2] with\n" +
            "        left paths.\n" +
            "\n" +
            "    -r, --ZhangShashaRight\n" +
            "        like \"-s right\". Use the algorithm by Zhang and Shasha [2] with\n" +
            "        right paths.\n" +
            "\n" +
            "    -k, --Klein\n" +
            "        like \"-s heavy\". Use the algorithm by Klein [3], which uses\n" +
            "        heavy paths.\n" +
            "\n" +
            "    -d, --Demaine\n" +
            "        like \"-s heavy -w\". Use the algorithm by Demaine [4], which\n" +
            "        uses heavy paths and always decomposes the larger tree.\n" +
            "\n" +
            "    -o, --RTED\n" +
            "        use the RTED algorithm by Pawlik and Augsten [1]. This is the\n" +
            "        default strategy.\n" +
            "\n" +
            "    -m, --mapping\n" +
            "        compute the minimal edit mapping between two trees. There might\n" +
            "        be multiple minimal edit mappings. This option computes only one\n" + 
            "        of them. The frst line of the output is the cost of the mapping.\n" +
            "        The following lines represent the edit operations. n and m are\n" +
            "        postorder IDs (beginning with 1) of nodes in the left-hand and\n" +
            "        the rigt-hand trees respectively.\n" +
            "            n->m - rename node n to m\n" +
            "            n->0 - delete node n\n" +
            "            0->m - insert node m\n" +
            "\n" +
    		"EXAMPLES\n" +
    		"\n" +
    		"    java -jar RTED_v0.1.jar -t {a{b}{c}} {a{b{d}}} -c 1 1 0.5 -s heavy --switch\n" +
    		"    java -jar RTED_v0.1.jar -f 1.tree 2.tree -s left\n" +
    		"    java -jar RTED_v0.1.jar -t {a{b}{c}} {a{b{d}}} --ZhangShashaLeft -v\n" +
    		"\n" +
    		"REFERENCES\n" +
    		"\n" +
    		"    [1] M. Pawlik and N. Augsten. RTED: a robust algorithm for the\n" +
    		"        tree edit distance. Proceedings of the VLDB Endowment\n" +
    		"        (PVLDB). 2012 (To appear).\n" +
    		"    [2] K.Zhang and D.Shasha. Simple fast algorithms for the editing\n" +
    		"        distance between trees and related problems. SIAM\n" +
    		"        J. Computing. 1989.\n" +
    		"    [3] P.N. Klein. Computing the edit-distance between unrooted\n" +
    		"        ordered trees.  European Symposium on Algorithms (ESA). 1998.\n" +
    		"    [4] E.D. Demaine, S. Mozes, B. Rossman and O. Weimann. An optimal\n" +
    		"        decomposition algorithm for tree edit distance. ACM Trans. on\n" +
    		"        Algorithms. 2009.\n" +
    		"\n" +
    		"AUTHORS\n" +
    		"\n" +
    		"    Mateusz Pawlik, Nikolaus Augsten";
	
	private String wrongArgumentsMessage = "Wrong arguments. Try \"java -jar RTED.jar --help\" for help.";
	
	private LblTree lt1, lt2;
	private int size1, size2;
	private boolean run, custom, array, strategy, ifSwitch, sota, verbose, demaine, mapping;
	private int sotaStrategy;
	private String customStrategy, customStrategyArrayFile;
	private RTED_InfoTree_Opt rted;
	private double ted;
	
	/**
	 * Main method 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		RTEDCommandLine rtedCL = new RTEDCommandLine();
		rtedCL.runCommandLine(args);
	}
	
	/**
	 * Run the command line with given arguments.
	 * 
	 * @param args
	 */
	public void runCommandLine(String[] args) {
		rted = new RTED_InfoTree_Opt(1, 1, 1);
	
		try {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--help") || args[i].equals("-h")) {
				System.out.println(helpMessage);
				System.exit(0);
			} else if (args[i].equals("-t") || args[i].equals("--trees")) {
				parseTreesFromCommandLine(args[i+1], args[i+2]);
				i = i+2;
				run = true;
			} else if (args[i].equals("-f") || args[i].equals("--files")) {
				parseTreesFromFiles(args[i+1], args[i+2]);
				i = i+2;
				run = true;
			} else if (args[i].equals("-l") || args[i].equals("--ZhangShashaLeft")) {
				sota = true;
				sotaStrategy = 0;
				strategy = true;
			} else if (args[i].equals("-r") || args[i].equals("--ZhangShashaRight")) {
				sota = true;
				sotaStrategy = 1;
				strategy = true;
			} else if (args[i].equals("-k") || args[i].equals("--Klein")) {
				sota = true;
				sotaStrategy = 2;
				strategy = true;
			} else if (args[i].equals("-d") || args[i].equals("--Demaine")) {
				sota = true;
				demaine = true;
				sotaStrategy = 2;
				strategy = true;
			} else if (args[i].equals("-o") || args[i].equals("--RTED")) {
				// do nothing - this is the default option
			} else if (args[i].equals("-s") || args[i].equals("--strategy")) {
				custom = true;
				customStrategy = args[i+1];
				i = i+1;
				strategy = true;
			} else if (args[i].equals("-a") || args[i].equals("--strategy-array")) {
				array = true;
				customStrategyArrayFile = args[i+1];
				i = i+1;
				strategy = true;
			} else if (args[i].equals("-w") || args[i].equals("--switch")) {
				ifSwitch = true;
			} else if (args[i].equals("-c") || args[i].equals("--costs")) {
				setCosts(args[i+1], args[i+2], args[i+3]);
				i = i+3;
			} else if (args[i].equals("-v") || args[i].equals("--verbose")) {
				verbose = true;
			} else if (args[i].equals("-m") || args[i].equals("--mapping")) {
				mapping = true;
			} else {
				System.out.println(wrongArgumentsMessage);
				System.exit(0);
			}
		}
		
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Too few arguments.");
            System.exit(0);
		}
		
		if (!run) {
			System.out.println(wrongArgumentsMessage);
			System.exit(0);
		}
		
		long time1 = (new Date()).getTime();
		if (strategy) {
			if (sota) {
				if (demaine) {
					setStrategy(sotaStrategy, true);
				} else {
					setStrategy(sotaStrategy, false);
				}
				ted = rted.nonNormalizedTreeDist();
			} else if (custom){
				setStrategy(customStrategy, ifSwitch);
				ted = rted.nonNormalizedTreeDist();
			} else if (array) {
				setStrategy(customStrategyArrayFile);
				ted = rted.nonNormalizedTreeDist();
			}
		} else {
			rted.computeOptimalStrategy();
			ted = rted.nonNormalizedTreeDist();
		}
		long time2 = (new Date()).getTime();
		if (verbose) {
			System.out.println("distance:             " + ted);
			System.out.println("runtime:              " + ((time2 - time1) / 1000.0));
			System.out.println("relevant subproblems: " + rted.counter);
			System.out.println("recurence steps:      "	+ rted.strStat[3]);
			System.out.println("left paths:           " + rted.strStat[0]);
			System.out.println("right paths:          " + rted.strStat[1]);
			System.out.println("heavy paths:          "	+ rted.strStat[2]);
		} else {
			System.out.println(ted);
		}
		if (mapping) {
			LinkedList<int[]> editMapping = rted.computeEditMapping();
			for (int[] nodeAlignment : editMapping) {
			    System.out.println(nodeAlignment[0] + "->" + nodeAlignment[1]);
			}
		}
	}
		
	/**
	 * Parse two input trees from the command line.
	 * 
	 * @param ts1
	 * @param ts2
	 */
	private void parseTreesFromCommandLine(String ts1, String ts2) {
		try {
            lt1 = LblTree.fromString(ts1);
            size1 = lt1.getNodeCount();
        } catch (Exception e) {
            System.out.println("TREE1 argument has wrong format");
            System.exit(0);
        }
        try {
            lt2 = LblTree.fromString(ts2);
            size2 = lt2.getNodeCount();
        } catch (Exception e) {
            System.out.println("TREE2 argument has wrong format");
            System.exit(0);
        }
        rted.init(lt1, lt2);
	}
	
	/**
	 * Parse two input trees from given files.
	 * 
	 * @param fs1
	 * @param fs2
	 */
	private void parseTreesFromFiles(String fs1, String fs2) {
		try {
            lt1 = LblTree.fromString((new BufferedReader(new FileReader(fs1))).readLine());
            size1 = lt1.getNodeCount();
        } catch (Exception e) {
            System.out.println("TREE1 argument has wrong format");
            System.exit(0);
        }
        try {
            lt2 = LblTree.fromString((new BufferedReader(new FileReader(fs2))).readLine());
            size2 = lt2.getNodeCount();
        } catch (Exception e) {
            System.out.println("TREE2 argument has wrong format");
            System.exit(0);
        }
        rted.init(lt1, lt2);
	}
	
	/**
	 * Set custom costs.
	 * 
	 * @param cds
	 * @param cis
	 * @param cms
	 */
	private void setCosts(String cds, String cis, String cms) {
		try {
			rted.setCustomCosts(Double.parseDouble(cds), Double.parseDouble(cis), Double.parseDouble(cms));
		} catch (Exception e) {
			System.out.println("One of the costs has wrong format.");
            System.exit(0);
		}
	}
	
	/**
	 * Set the strategy to be entirely of the type given by str.
	 * 
	 * @param str strategy type
	 * @param ifSwitch if set to true the strategy will be applied to the currently bigger tree 
	 */
	private void setStrategy(String str, boolean ifSwitch) {
		if (str.equals("left")) {
			rted.setCustomStrategy(0, ifSwitch);
		} else if (str.equals("right")) {
			rted.setCustomStrategy(1, ifSwitch);
		} else if (str.equals("heavy")) {
			rted.setCustomStrategy(2, ifSwitch);
		} else {
			System.out.println("Wrong strategy.");
            System.exit(0);
		}
	}
	
	/**
	 * Set the strategy to be entirely of the type given by str.
	 * 
	 * @param str strategy type
	 * @param ifSwitch if set to true the strategy will be applied to the currently bigger tree 
	 */
	private void setStrategy(int str, boolean ifSwitch) {
		try {
			rted.setCustomStrategy(str, ifSwitch);
		} catch (Exception e) {
			System.out.println("Strategy has wrong format.");
            System.exit(0);
		}
	}
	
	/**
	 * Set the strategy to the one given in strArrayFile.
	 * 
	 * @param strArrayFile path to the file with the strategy
	 */
	private void setStrategy(String strArrayFile) {
		try {
			rted.setCustomStrategy(parseStrategyArrayString(strArrayFile));
		} catch (Exception e) {
			System.out.println("Strategy has wrong format.");
            System.exit(0);
		}
	}
	
	/**
	 * Parse the strategy array.
	 * 
	 * Array String format:
	 * ? ? ? ?
	 * ? ? ? ? 
	 * ? ? ? ? 
	 * 
	 * @param strategyArray
	 * @return
	 */
	private int[][] parseStrategyArrayString(String fileWithStrategyArray) {
		int[][] str = null;
		Vector<int[]> strVector = new Vector<int[]>();
		int[] strLine;
		String line;
		Scanner s;
		int value;
		BufferedReader br;
	
		try {
			br = new BufferedReader(new FileReader(fileWithStrategyArray));
			line = br.readLine();
			int index = 0;
			while (line != null) {
				s = new Scanner(line);
				strLine = new int[(line.length()+1)/2];
				if (strLine.length != size2) {
					System.err.println("Trees sizes differ from the strategy array dimensions.");
					System.exit(0);
				}
				int i = 0;
				while (s.hasNextInt()) {
					value = s.nextInt();
					if (value != 0 && value != 1 && value != 2 && value != 4 && value != 5 && value != 6) {
						System.out.println("Strategy value at position " + index + " in the strategy array file is wrong.");
						System.exit(0);
					}
					index++;
					strLine[i] = value;
					i++;
				}
				strVector.add(strLine);
				line = br.readLine();
			}
			str = new int[strVector.size()][];
			int i = 0;
			for (int[] l : strVector) {
				str[i] = l;
				i++;
			}			
			if (str.length != size1) {
				System.err.println("Trees sizes differ from the strategy array dimensions.");
				System.exit(0);
			}
			br.close();
		} catch (Exception e) {
			System.out.println("Something is wrong with strategy array file.");
			System.exit(0);
		}
			
		return str;
	}
}
