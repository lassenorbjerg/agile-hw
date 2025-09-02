## Values and Variables
### Create an immutable value pi with value 3.14159.

scala> val pi = 3.14159
val pi: Double = 3.14159

### Create a mutable variable counter initialized to 0. Increment it by 1.

scala> var counter = 0
var counter: Int = 0

scala> counter += 1

### Try reassigning pi — what happens? Why?

scala> counter
val res0: Int = 1

scala> pi = 3.0
-- [E052] Type Error: ----------------------------------------------------------
1 |pi = 3.0
  |^^^^^^^^
  |Reassignment to val pi
  |
  | longer explanation available when compiling with `-explain`
1 error found

it is immutable, so it cannot be modified


## Functions and Conditionals

* Write a function `max(a: Int, b: Int): Int` that returns the larger of two integers using an if expression.

def max(a: Int, b: Int): Int = {
     |  if (a > b) {
     |      return a
     |  }
     |  return b
     | }


* Write a recursive function `fib(n: Int): Int` that computes the n-th Fibonacci number. Test it with `fib(5)` (should be 15).


def fib(n: Int): Int = {
     |  if (n <= 1) {
     |      return 1
     |  }
     |  return n + fib(n-1)
     | }


### Collections

Create a list:
```scala
val numbers = List(1, 2, 3, 4, 5)
```
* Print the head and tail of the list.

scala> numbers.head
val res0: Int = 1

scala> numbers.tail
val res1: List[Int] = List(2, 3, 4, 5)

* Append the number 7 at the end.


val numbers2 = numbers :+ 7
val numbers2: List[Int] = List(1, 2, 3, 4, 5, 7)
