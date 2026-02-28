package com.gvc;

import java.util.Random;
import java.util.Scanner;

/**
 * 简易Java抽卡程序
 * 稀有度概率：普通70%、稀有20%、史诗8%、传说2%
 * 十连抽保底：至少出1张稀有及以上卡牌
 */
public class CardDraw {
    // 定义卡牌稀有度枚举（方便管理）
    enum Rarity {
        COMMON("普通", 70),    // 普通，概率70%
        RARE("稀有", 20),      // 稀有，概率20%
        EPIC("史诗", 8),       // 史诗，概率8%
        LEGENDARY("传说", 2);  // 传说，概率2%

        private final String name;  // 稀有度名称
        private final int probability;  // 概率（百分比）

        Rarity(String name, int probability) {
            this.name = name;
            this.probability = probability;
        }

        public String getName() {
            return name;
        }

        public int getProbability() {
            return probability;
        }
    }

    // 随机数生成器
    private static final Random random = new Random();
    // 扫描器（接收用户输入）
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * 单抽方法：随机抽取一张卡牌
     * @return 抽到的卡牌稀有度
     */
    public static Rarity drawSingleCard() {
        // 生成1-100的随机数（对应概率百分比）
        int randomNum = random.nextInt(100) + 1;
        int cumulativeProb = 0;

        // 遍历稀有度，判断随机数落在哪个概率区间
        for (Rarity rarity : Rarity.values()) {
            cumulativeProb += rarity.getProbability();
            if (randomNum <= cumulativeProb) {
                return rarity;
            }
        }

        // 兜底（理论上不会执行到）
        return Rarity.COMMON;
    }

    /**
     * 十连抽方法：抽10次，保底至少1张稀有及以上
     */
    public static void drawTenCards() {
        System.out.println("===== 开始十连抽 =====");
        Rarity[] results = new Rarity[10];
        boolean hasRareOrAbove = false;

        // 先抽10次
        for (int i = 0; i < 10; i++) {
            results[i] = drawSingleCard();
            // 检查是否有稀有及以上卡牌
            if (results[i] != Rarity.COMMON) {
                hasRareOrAbove = true;
            }
        }

        // 如果没有稀有及以上，替换最后一张为稀有（保底）
        if (!hasRareOrAbove) {
            results[9] = Rarity.RARE;
        }

        // 输出十连抽结果
        for (int i = 0; i < 10; i++) {
            System.out.printf("第%d抽：%s卡牌%n", i + 1, results[i].getName());
        }
        System.out.println("===== 十连抽结束 =====");
    }

    public static void main(String[] args) {
        System.out.println("=== 简易抽卡程序 ===");
        System.out.println("稀有度概率：普通70% | 稀有20% | 史诗8% | 传说2%");
        System.out.println("十连抽保底：至少1张稀有及以上卡牌");

        // 循环接收用户操作
        while (true) {
            System.out.println("\n请选择操作：");
            System.out.println("1. 单抽");
            System.out.println("2. 十连抽");
            System.out.println("3. 退出程序");
            System.out.print("输入数字选择：");

            // 接收用户输入（处理非数字输入的异常）
            int choice;
            try {
                choice = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("输入错误！请输入1、2、3中的数字");
                scanner.nextLine(); // 清空错误输入
                continue;
            }

            // 根据选择执行对应操作
            switch (choice) {
                case 1:
                    Rarity singleResult = drawSingleCard();
                    System.out.printf("单抽结果：抽到【%s】卡牌！%n", singleResult.getName());
                    break;
                case 2:
                    drawTenCards();
                    break;
                case 3:
                    System.out.println("退出程序，感谢使用！");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("输入错误！请选择1、2、3");
            }
        }
    }
}
