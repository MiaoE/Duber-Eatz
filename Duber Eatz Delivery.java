import java.io.File;
import java.util.Scanner;
import java.io.PrintWriter;

/* DuberEatzDelivery.java
 * Eric Miao
 * November 5, 2019
 * Duber wants to send deliveries, but we have to determine the shortest number of steps Duber has to take in order
 * to each the destination
 * Version: 2.4+ (Unit, Level) ALTERNATE
 */

class DuberEatzDeliveryL4Plus{
  static int stepMade; //Number of steps made between two positions
  static char[][] finalMap; //Final map
  static char[][] tempMap; //Temp map to store route between two positions
  static int mapLength, mapWidth; //Map's length and width
  static int maxValue; //Max locations in map
  static int finalStep; //Final minimum steps
  static char[][] routeMap;//Route map to store route from start to end
  static int tips; //Total tips

  public static void main(String[] args) throws Exception {//main class
    Scanner in = new Scanner(System.in);
    String fileName = in.next() + ".txt";//text file
    File m = new File(fileName);
    Scanner file = new Scanner(m);
    mapLength = file.nextInt();
    mapWidth = file.nextInt();//determine the length and width
    //Create variables according to map length and width
    maxValue = mapLength * mapWidth;
    finalMap = new char[mapLength][mapWidth];
    tempMap = new char[mapLength][mapWidth];
    routeMap = new char[mapLength][mapWidth];

    file.nextLine();//**avoid nextLineBlues**
    int playerX = 0, playerY = 0, locationX = 0, locationY = 0, microwave = 0;
    //initialize the map and all objects' locations
    char[][] map = new char[mapLength][mapWidth];
    int[][] tempSet = new int[maxValue][2];
    int desNum = 0; //Number of destinations
    for (int i = 0; i < mapLength; i++) {
      String line = file.nextLine();
      for (int j = 0; j < mapWidth; j++) {
        map[i][j] = line.charAt(j);
        if (map[i][j] == 'S') {
          playerX = i;
          playerY = j;
        } else if (map[i][j] != ' ' && map[i][j] != '#' && map[i][j] != 'M') {
          tempSet[desNum][0] = i;
          tempSet[desNum][1] = j;
          desNum++;
        }
      }
    }
    file.close();

    int[][] desSet = new int[desNum][3]; //Destination Set in the map

    for (int i = 0; i < desNum; i++) {//Create destinations from temporary set
      desSet[i][0] = tempSet[i][0];
      desSet[i][1] = tempSet[i][1];
      desSet[i][2] = Character.getNumericValue(map[desSet[i][0]][desSet[i][1]]);//Tip in this position
    }

    finalStep = maxValue; //Initial final steps to max value
    tips = maxValue*-10;

    checkAll(map, desSet, playerX, playerY, 0);//determine the route

    System.out.printf("It takes %d steps to get reach final destination!\n", finalStep); //Output result
    System.out.printf("The max tip is %d!\n", tips); //Output result
    printMap(finalMap); //Print result Map
    createPPMfile(finalMap);
  }

  public static int checkAll(char[][] map, int[][] desSet, int startX, int startY, int count) {
    if (count == desSet.length) {
      // Calculate this route steps
      int steps = 0;
      stepMade = maxValue;
      path(map, startX, startY, desSet[0][0], desSet[0][1], steps);
      for (int i = 0; i < mapLength; i++) { //Add last step to the route map
        for (int j = 0; j < mapWidth; j++) {
          if (routeMap[i][j] != '#' && routeMap[i][j] != 'x' && routeMap[i][j] != 'S' && routeMap[i][j] != 'X') {
            routeMap[i][j] = tempMap[i][j];
          }
        }
      }

      int sum = stepMade; //Total steps
      int tip = calTip(sum, desSet[0][2]);

      for (int i = 1; i < desSet.length; i++) {
        steps = 0;
        stepMade = maxValue;
        path(map, desSet[i - 1][0], desSet[i - 1][1], desSet[i][0], desSet[i][1], steps);
        sum += stepMade;
        tip += calTip(sum, desSet[i][2]);

        for (int k = 0; k < mapLength; k++) { //Add this route to the route map
          for (int j = 0; j < mapWidth; j++) {
            if (routeMap[k][j] != '#' && routeMap[k][j] != 'x' && routeMap[k][j] != 'S' && routeMap[k][j] != 'X') {
              routeMap[k][j] = tempMap[k][j];
            }
          }
        }
      }
      // Record the max tip route
      if (tip > tips) {
        finalStep = sum;
        tips = tip;
        for (int i = 0; i < mapLength; i++) { //Copy current route map to the final map
          for (int j = 0; j < mapWidth; j++) {
            finalMap[i][j] = routeMap[i][j];
          }
        }
      }
      return finalStep;
    }
    // All possible route
    for (
        int i = count;
        i < desSet.length; i++) {
      swap(desSet, count, i);
      checkAll(map, desSet, startX, startY, count + 1);
      swap(desSet, count, i);
    }
    return finalStep;
  }

  public static void swap(int[][] desSet, int i, int j) {
    if (i == j) {
      return;
    }
    int tempX = desSet[i][0];
    int tempY = desSet[i][1];
    desSet[i][0] = desSet[j][0];
    desSet[i][1] = desSet[j][1];
    desSet[j][0] = tempX;
    desSet[j][1] = tempY;
  }

  //Find the minimum route from {playerX, playerY} to {desX, desY}
  public static void path(char[][] map, int playerX, int playerY, int desX, int desY, int steps) {
    if (playerX == desX && playerY == desY) {//Recursion ending condition
      if (steps < stepMade) {
        stepMade = steps;
        for (int i = 0; i < mapLength; i++) {//Copy minimum route to temporary map
          for (int j = 0; j < mapWidth; j++) {
            tempMap[i][j] = map[i][j];
          }
        }
        tempMap[desX][desY] = 'X';
      }
      return;
    }
    int nextX, nextY;
    for (int k = 0; k <= 3; k++) {
      if (k == 0) {
        nextX = playerX;
        nextY = playerY + 1;//Move Right
      } else if (k == 1) {
        nextX = playerX + 1;//Move Down
        nextY = playerY;
      } else if (k == 2) {
        nextX = playerX;
        nextY = playerY - 1;//Move Left
      } else {
        nextX = playerX - 1;//Move Up
        nextY = playerY;
      }

      //Recusive to find the route to {desX, desY}
      if (map[nextX][nextY] != 'x' && map[nextX][nextY] != '#' && map[nextX][nextY] != 'S') {
        map[nextX][nextY] = 'x';
        path(map, nextX, nextY, desX, desY, steps + 1);
        map[nextX][nextY] = ' ';
      }
    }
  }

  public static void printMap(char[][] map) {
    for (int i = 0; i < mapLength; i++) {
      System.out.println(map[i]);
    }
  }

  public static int calTip(int step, int tip) {
    int i = tip - step;
    if (i < 0)
      return i;
    else
      return i*10;
  }
  
  public static void createPPMfile(char[][] map) throws Exception{
    Scanner name = new Scanner(System.in);
    System.out.println("What is the name of the PPM file?");
    String fileName = name.next() + ".ppm";
    PrintWriter file = new PrintWriter(new File(fileName));
    
    file.println("P3");
    file.println(mapLength*20 + " " + mapWidth*20);//*20
    file.println("255");
    for(int i = 0; i < mapLength; i++){
      for(int k = 0; k < 20; k++){
        for(int j = 0; j < mapWidth; j++){
          for(int l = 0; l < 20; l++){
          if(map[i][j] == '#') file.print("0  0  0  ");
          else if(map[i][j] == 'x') file.print("255 0 0  ");
          else if(map[i][j] == 'X') file.print("0 0 255  ");
          else if(map[i][j] == 'S') file.print("0 255 0  ");
          else file.print("255 255 255 ");
          }
        }
        file.println();
      }
    }
    file.close();
  }
}