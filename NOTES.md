# Notes

## Networking

- https://netbeez.net/blog/how-to-use-the-linux-traffic-control/
- https://www.cs.unm.edu/~crandall/netsfall13/TCtutorial.pdf

<pre>
tc qdisc add dev eth0 root netem delay 5000ms

tc qdisc add dev eth0 root netem loss 90%

tc qdisc del root dev eth0
tc qdisc show  dev eth0
</pre>

## Akka Streams

- https://medium.com/wbaa/akka-http-client-meets-zio-810781827ec