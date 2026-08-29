package pl.v3bc.platform.utils;

import net.kyori.adventure.text.Component;
import pl.v3bc.platform.utils.adventure.NekoChat;

/**
 * @Author: v3bc_
 * @Date: 8/26/26
 * @Project: astra-platform
 */

public class ProgressUtil {
    public static Component bar(long intActually, long intRequired, int numberOfBlocks) {
        int i;
        intActually = Math.min(intActually, intRequired);
        double progressPercentage = (double) intActually / (double) intRequired;
        int filledBlocks = (int) Math.round(progressPercentage * (double) numberOfBlocks);
        StringBuilder progressBar = new StringBuilder();
        for (i = 0; i < filledBlocks; ++i) {
            progressBar.append("&a⏹");
        }
        for (i = filledBlocks; i < numberOfBlocks; ++i) {
            progressBar.append("&c⏹");
        }
        return NekoChat.component(progressBar.toString());
    }

    public static Component bar(int intActually, int intRequired, int numberOfBlocks) {
        int i;
        intActually = Math.min(intActually, intRequired);
        double progressPercentage = (double) intActually / (double) intRequired;
        int filledBlocks = (int) Math.round(progressPercentage * (double) numberOfBlocks);
        StringBuilder progressBar = new StringBuilder();
        for (i = 0; i < filledBlocks; ++i) {
            progressBar.append("&a⏹");
        }
        for (i = filledBlocks; i < numberOfBlocks; ++i) {
            progressBar.append("&c⏹");
        }
        return NekoChat.component(progressBar.toString());
    }

    public static String percent(int intActually, int intRequired) {
        double progressPercentage = (double) Math.min(intActually, intRequired) / (double) intRequired * 100.0;
        int roundedPercentage = (int) Math.round(progressPercentage);
        if (roundedPercentage > 100) {
            roundedPercentage = 100;
        }
        return roundedPercentage + "%";
    }

    public static float calculateBarProgress(long remainingTime, long totalTime) {
        if (totalTime <= 0L) {
            return 0.0f;
        }
        float progress = (float) remainingTime / (float) totalTime;
        return Math.max(0.0f, Math.min(1.0f, progress));
    }
}
