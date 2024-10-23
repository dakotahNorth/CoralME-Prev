# CoralME
A simple, fast and garbage-free matching engine order book that you can use as a starting point for your matching engines.

## What is it?
CoralME is an order book data-structure that matches orders based on price-time priority. It maintains limit orders resting in an order book until they are either canceled or filled. Whenever an order changes its state, a callback is issued to registered listeners.

## What people usually mean by the term _Matching Engine_?
Usually when people talk about a _Matching Engine_, what they are really referring to is the full solution for an electronic exchange. That would include gateways, drop copies, market data, balances, reports, monitors, margins, compliance, fees, etc. Plus the _messaging middleware_ to tie all these pieces together. In that context, **the matching engine is really just one of the many parts of an electronic exchange**. It is an important part, the central nervous systems of an exchange, which maintains orders resting inside order books, and match them when liquidity takers meet liquidity providers (i.e. market makers).

For a detailed discussion of how a **first-class electronic exchange** can be built from the ground up using the sequencer architecture you should refer to [this article](https://www.coralblocks.com/index.php/building-a-first-class-exchange-architecture-with-coralsequencer/).

## Quick Start
Refer to [Example.java](https://github.com/coralblocks/CoralME/blob/main/src/main/java/com/coralblocks/coralme/example/Example.java) for a bunch of order matching use-cases.

The [OrderBookTest.java](https://github.com/coralblocks/CoralME/blob/main/src/test/java/com/coralblocks/coralme/OrderBookTest.java) might give you some good ideas as well but I find the [Example.java](https://github.com/coralblocks/CoralME/blob/main/src/main/java/com/coralblocks/coralme/example/Example.java) easier to follow.

## Features
- Fast
- Garbage-free
- Callback oriented using standard Java Consumer interface
- Price levels
- Price improvement for fills
- MARKET and LIMIT order types
- IOC, GTC and DAY
- MAKER (of liquidity) and TAKER (of liquidity) execution sides
- NORMAL, CROSSED, LOCKED, ONESIDED and EMPTY book states
- ClientID, ClientOrderID and OrderID
- ExecutionID and ExecutionMatchID
- Can optionally check and disallow trade to self
- Memory monitoring with available memory callback

## How can I check that it is zero garbage?
Check [NoGCTest.java](https://github.com/coralblocks/CoralME/blob/main/src/main/java/com/coralblocks/coralme/example/NoGCTest.java) to see that it creates a book and populates this book with 10 orders _one million times_. And on each of these one million times it does a bunch of executions, rejects, cancelations, reduces, etc. Run this test with `-verbose:gc -Xms128m -Xmx256m` and you will always see **zero GC activity**. _No matter how many iterations you perform, the gc activity is always zero_. If you want to see some GC activity, you can turn on a flag that forces the creation of garbage by [producing some strings](https://github.com/coralblocks/CoralME/blob/bb9461313537987db43339e429b7314e58bbb784/src/main/java/com/coralblocks/coralme/example/NoGCTest.java#L103) in the middle of the loop.

##### Creating ZERO garbage
```
$ ./bin/runGCTest.sh
java -verbose:gc -Xms128m -Xmx256m -cp target/classes com.coralblocks.coralme.example.NoGCTest false 1000000
1000000 ... DONE!
```
##### Forcing the creation of garbage (pass true to runGCTest.sh)
```
$ ./bin/runGCTest.sh true
java -verbose:gc -Xms128m -Xmx256m -cp target/classes com.coralblocks.coralme.example.NoGCTest true 1000000
60870[GC (Allocation Failure)  33280K->1224K(125952K), 0.0005392 secs]
146061[GC (Allocation Failure)  34504K->1200K(125952K), 0.0005032 secs]
231254[GC (Allocation Failure)  34480K->1240K(125952K), 0.0003991 secs]
316449[GC (Allocation Failure)  34520K->1208K(125952K), 0.0004686 secs]
401642[GC (Allocation Failure)  34488K->1264K(125952K), 0.0004315 secs]
486832[GC (Allocation Failure)  34544K->1200K(129536K), 0.0004712 secs]
590373[GC (Allocation Failure)  41648K->1128K(129536K), 0.0005286 secs]
693917[GC (Allocation Failure)  41576K->1128K(128512K), 0.0002270 secs]
794836[GC (Allocation Failure)  40552K->1128K(129024K), 0.0002390 secs]
895759[GC (Allocation Failure)  40552K->1128K(129024K), 0.0002202 secs]
996679[GC (Allocation Failure)  40552K->1128K(129024K), 0.0002492 secs]
1000000 ... DONE!
```

## Callbacks Supported
```Java
public interface OrderBookListener {

    public void onOrderReduced(OrderBook orderBook, long time, Order order,
                                 long reduceNewTotalSize);

    public void onOrderCanceled(OrderBook orderBook, long time, Order order,
                                  CancelReason cancelReason);

    public void onOrderExecuted(OrderBook orderBook, long time, Order order,
                                  ExecuteSide executeSide, long executeSize,
                                  long executePrice, long executeId, long executeMatchId);

    public void onOrderAccepted(OrderBook orderBook, long time, Order order);

    public void onOrderRejected(OrderBook orderBook, long time, Order order,
                                  RejectReason rejectReason);

    public void onOrderRested(OrderBook orderBook, long time, Order order,
                                long restSize, long restPrice);

    public void onOrderTerminated(OrderBook orderBook, long time, Order order);

}
```

## Code Snippet
```Java
import java.util.function.Consumer;

// ... (previous code remains the same)

// Example of using MemoryMonitor with Consumer<Long>
Consumer<Long> memoryCallback = availableMemory -> {
    System.out.println("Available memory: " + availableMemory + " bytes");
};

MemoryMonitor memoryMonitor = new MemoryMonitor(memoryCallback, 1000); // Check every 1 second
memoryMonitor.start();

// ... (rest of the code remains the same)
```

// ... (rest of the README content remains the same)
    
 ```
 

 
 






