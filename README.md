# AtomicBenchmark

Simple benchmark for atomic access in Java with different methods 
(AtomicInteger, LongAdder, ReentrantLock, synchronized block and without synchronization).

The analyse.R script plots the results as it can be seen below.

```R
library(ggplot2)
data <- read.csv("timing_data.txt", sep=",", h=F)
colnames(data) <- c("time", "method")
ggplot(data, aes(time, group=method))+
  geom_density(aes(color=method))+
  xlim(0,2000)+
  labs(x="time(ms)")
```

![Timing plots](https://raw.githubusercontent.com/bdomokos74/AtomicBenchmark/master/timing_data.png)
