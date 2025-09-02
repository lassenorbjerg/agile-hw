class Person(name: String, age: Int) {
  def greet() : String = {
      return "hi " + name + " you are " + age + " years old"
  }
}


object Person extends App {
    val person = new Person("lasse", 23)
    println(person.greet())
}
