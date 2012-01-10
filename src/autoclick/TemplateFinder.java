package autoclick;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TemplateFinder {

  public static Map<Integer, Integer> createColorMap(BufferedImage img){
    Map<Integer, Integer> colors = new HashMap<Integer, Integer>();
    for(int x = 0; x < img.getWidth(); x++){
      for(int y = 0; y < img.getHeight(); y++){
        int rgb = img.getRGB(x, y);
        colors.put(rgb, colors.containsKey(rgb) ? colors.get(rgb) + 1 : 1);
      }
    }
    return colors;
  }

  public static Map<Integer, Integer> createRandomSampleColorMap(BufferedImage img, int n){
    Map<Integer, Integer> colors = new HashMap<Integer, Integer>();
    for(int i = 0; i < n; i++){
      int x = (int) (Math.random() * img.getWidth());
      int y = (int) (Math.random() * img.getHeight());
      int rgb = img.getRGB(x, y);
      colors.put(rgb, colors.containsKey(rgb) ? colors.get(rgb) + 1 : 1);
    }
    return colors;
  }

  /** img の中から t を探す. 見つかったら左上点, 見つからなかったら null. **/
  public static Point lookup(BufferedImage img, BufferedImage t){
    // 1. template の特徴となる色を調べる
    Map<Integer, Integer> imgMap = createRandomSampleColorMap(img, 1000);
    Map<Integer, Integer> tMap = createColorMap(t); // TODO: cache しておく方がよい
    for(int rgb : tMap.keySet()){
      if(imgMap.containsKey(rgb)){
        tMap.put(rgb, tMap.get(rgb) / imgMap.get(rgb));
      }
    }

    // 2. 特徴色を使って t が一番含まれていそうな領域を絞り込む
    // 2-1. t と同じサイズの領域に分割して、それぞれの領域のマッチ度を計算する
    int tw = t.getWidth(), th = t.getHeight();
    int nw = img.getWidth() / tw, nh = img.getHeight() / th;
    int[][] score = new int[nw][nh];
    for(int i = 0; i < nw; i++){
      for(int j = 0; j < nh; j++){
        score[i][j] = matchScore(tMap, img, i * tw, j * th, tw, th);
      }
    }

    // 2-2. (2-1) の領域サイズだと複数の領域にまたがる場合があるので、その 4 倍の大きさの領域で考える
    Map<Point, Integer> ranking = new HashMap<Point, Integer>();
    for(int i = 0; i < nw - 1; i++){
      for(int j = 0; j < nh - 1; j++){
        int n = score[i][j] + score[i + 1][j] + score[i][j + 1] + score[i + 1][j + 1];
        ranking.put(new Point(i * tw, j * th), n);
      }
    }

    // 3. 絞り込んだ領域をしらみつぶしに調べる
    int n = nw * nh / 50;
    ArrayList<Map.Entry<Point, Integer>> list = new ArrayList<Map.Entry<Point, Integer>>(ranking.entrySet());
    Collections.sort(list, new EntryComparator());
    for(int i = 0; i < n; i++){
      Point p = list.get(i).getKey();
      p =  naiveLookup(img, t, p.x, p.y, tw * 2, th * 2);
      if(p != null) return p;
    }
    return null;
  }
  
  private static int matchScore(Map<Integer, Integer> colorMap, BufferedImage img, int sx, int sy, int w, int h){
    int xMax = Math.min(sx + w, img.getWidth());
    int yMax = Math.min(sy + h, img.getHeight());
    int score = 0;
    for(int x = sx; x < xMax; x += 2){
      for(int y = sy; y < yMax; y += 2){
        int rgb = img.getRGB(x, y);
        if(colorMap.containsKey(rgb)) score += colorMap.get(rgb);
      }
    }
    return score;
  }

  private static Point naiveLookup(BufferedImage img, BufferedImage t, int sx, int sy, int w, int h){
    int xMax = Math.min(sx + w, img.getWidth() - t.getWidth());
    int yMax = Math.min(sy + h, img.getHeight() - t.getHeight());
    for (int x = sx; x < xMax; x++) {
      for (int y = sy; y < yMax; y++) {
        if (checkMatch(img.getSubimage(x, y, t.getWidth(), t.getHeight()), t)) {
          return new Point(x, y);
        }
      }
    }
    return null;
  }

  public static Point naiveLookup(BufferedImage img, BufferedImage t) {
    for (int x = 0; x < img.getWidth() - t.getWidth(); x++) {
      for (int y = 0; y < img.getHeight() - t.getHeight(); y++) {
        if (checkMatch(img.getSubimage(x, y, t.getWidth(), t.getHeight()), t)) {
          return new Point(x, y);
        }
      }
    }
    return null;
  }

  public static boolean checkMatch(BufferedImage i1, BufferedImage i2) {
    if (i1.getWidth() != i2.getWidth() || i1.getHeight() != i2.getHeight()) {
      return false;
    }
    for (int i = 0; i < i1.getWidth(); i++) {
      for (int j = 0; j < i1.getHeight(); j++) {
        if (i1.getRGB(i, j) != i2.getRGB(i, j)) {
          return false;
        }
      }
    }
    return true;
  }
}
