package top.cuteworld.datagen.clickdata.model;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * mock emit
 */
@Slf4j
public class UserActionEmitter implements Runnable {

    public static final int[] P_IDS = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    public static final String[] ACTIONS = {"CLICK", "VIEW", "ADD_CART"};

    private static boolean stop_now = false;

    public static final Map<String, Long> EMIT_STATISTICS = new HashMap<>();
    public static long totalSize = 0;

    private Function<UserBehaviorItem, Void> sendOutFunction;

    public UserActionEmitter(Function<UserBehaviorItem, Void> function) {
        this.sendOutFunction = function;
    }

    public synchronized void emit() {
        int m = ACTIONS.length;
        int p_size = P_IDS.length;

        int pid = P_IDS[(int) (Math.random() * p_size)];
        log.trace("The pid is {}", pid);
        UserBehaviorItem userBehaviorItem = UserBehaviorItem.builder().action(ACTIONS[(int) Math.random() * m]).userId("u_1").eventTime(new Date()).productId("p_" + pid) //随机产生PID
                .build();
        try {
            sendOutFunction.apply(userBehaviorItem); //real send out
        } catch (Exception e) {
            log.error("failed to apply", e);
        }

        statistics(userBehaviorItem);
    }

    private void statistics(UserBehaviorItem userBehaviorItem) {
        Long aLong = EMIT_STATISTICS.get(userBehaviorItem.getProductId());
        if (aLong == null) {
            EMIT_STATISTICS.put(userBehaviorItem.getProductId(), 1l);
        } else {
            EMIT_STATISTICS.put(userBehaviorItem.getProductId(), aLong + 1l);
        }
    }

    @Override
    public void run() {
        while (true) {
            if (stop_now) {
                return;
            }
            emit();
            try {
                //随机暂停
                Thread.sleep(10 + (long) (Math.random() * 100l));
            } catch (InterruptedException e) {
            }
        }
    }

    public void batchEmit() {
        {
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            for (int i = 0; i < 5; i++) {
                executorService.submit(new UserActionEmitter(this.sendOutFunction));
            }
            System.out.println("Emitter is launched!!!");
            int batch_size = 0;
            while (!executorService.isShutdown()) {
                log.info("---- Statistics {} ----", batch_size);
                showStatistics();
//                if (batch_size > 3) {
//                    stop_now = true;
//                    executorService.shutdownNow();
//                    log.info("---- Final  Statistics ----");
//                    showStatistics();
//                    return;
//                }
                try {
                    Thread.sleep(4000l);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }
                batch_size++;
                log.info("---- Statistics ----");
            }
        }
    }


    private static synchronized void showStatistics() {
        long total = 0l;
        for (String key : EMIT_STATISTICS.keySet()) {
            long size = EMIT_STATISTICS.get(key);
            total = total + size;
            log.info("Product Id:" + key + "->" + EMIT_STATISTICS.get(key));
        }
        log.info("Total gen size {}", total);
    }

}
