library(ggplot2)
data <- read.csv("timing_data.txt", sep=",", h=F)
colnames(data) <- c("time", "method")
ggplot(data, aes(time, group=method))+
  geom_density(aes(color=method))+
  xlim(0,2000)+
  labs(x="time(ms)")
