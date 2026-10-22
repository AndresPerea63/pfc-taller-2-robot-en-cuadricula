package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RobotCuadriculaPropiasTest extends AnyFunSuite {

  val r = new RobotCuadricula()
  import r._

  val cuadrado = Repetir(4, Secuencia(List(Avanzar(1), Girar(Derecha))))


  // Punto 1: girar ------------------------------------------------------------

  test("girar propia: Este a la derecha da Sur") {
    assert(girar(Este, Derecha) == Sur)
  }

  test("girar propia: Norte a la izquierda da Oeste") {
    assert(girar(Norte, Izquierda) == Oeste)
  }

  test("girar propia: Sur a la derecha da Oeste") {
    assert(girar(Sur, Derecha) == Oeste)
  }

  test("girar propia: Oeste a la izquierda da Sur") {
    assert(girar(Oeste, Izquierda) == Sur)
  }

  test("girar propia: cuatro giros a la izquierda vuelven al Norte") {
    val resultado = List(Izquierda, Izquierda, Izquierda, Izquierda)
      .foldLeft(Norte: Direccion)((direccion, sentido) => girar(direccion, sentido))

    assert(resultado == Norte)
  }

  // Punto 2 -------------------------------------------------------------------

  test("ejecutar propia: avanzar al este aumenta x") {
    assert(ejecutar(Estado(2, 3, Este), Avanzar(4)) == Estado(6, 3, Este))
  }

  test("ejecutar propia: avanzar al sur desde el origen da y negativa") {
    assert(ejecutar(Estado(0, 0, Sur), Avanzar(3)) == Estado(0, -3, Sur))
  }

  test("ejecutar propia: repetir tres veces avanzar dos") {
    assert(ejecutar(inicial, Repetir(3, Avanzar(2))) == Estado(0, 6, Norte))
  }

  test("ejecutar propia: repetir un número negativo de veces no hace nada") {
    assert(ejecutar(inicial, Repetir(-2, Avanzar(5))) == inicial)
  }

  test("ejecutar propia: girar a la izquierda y avanzar va al oeste") {
    assert(ejecutar(inicial, Secuencia(List(Girar(Izquierda), Avanzar(3)))) == Estado(-3, 0, Oeste))
  }

  test("ejecutar propia: dos vueltas de avanzar y girar a la derecha terminan mirando al sur") {
    val o = Repetir(2, Secuencia(List(Avanzar(1), Girar(Derecha))))
    assert(ejecutar(inicial, o) == Estado(1, 1, Sur))
  }

  test("ejecutar propia: repeticiones anidadas multiplican los pasos") {
    assert(ejecutar(inicial, Repetir(2, Repetir(3, Avanzar(1)))) == Estado(0, 6, Norte))
  }

  // Punto 3: cuantasBasicas ---------------------------------------------------

  test("cuantasBasicas propia: un giro cuenta uno") {
    assert(cuantasBasicas(Girar(Izquierda)) == 1)
  }

  test("cuantasBasicas propia: repetir tres veces una básica cuenta tres") {
    assert(cuantasBasicas(Repetir(3, Avanzar(2))) == 3)
  }

  test("cuantasBasicas propia: una secuencia suma sus básicas") {
    assert(cuantasBasicas(Secuencia(List(Avanzar(1), Girar(Derecha), Avanzar(2)))) == 3)
  }

  test("cuantasBasicas propia: las repeticiones anidadas se multiplican") {
    assert(cuantasBasicas(Repetir(2, Repetir(3, Avanzar(1)))) == 6)
  }

  test("cuantasBasicas propia: repetir algo vacío no ejecuta nada") {
    assert(cuantasBasicas(Repetir(5, Secuencia(Nil))) == 0)
  }

  test("cuantasBasicas propia: repetir un número negativo de veces no ejecuta nada") {
    assert(cuantasBasicas(Repetir(-1, Avanzar(1))) == 0)
  }

  test("cuantasBasicas propia: el cuadrado más un avance suma nueve") {
    assert(cuantasBasicas(Secuencia(List(cuadrado, Avanzar(1)))) == 9)
  }

  // Punto 3: profundidad ------------------------------------------------------

  test("profundidad propia: un giro tiene profundidad 1") {
    assert(profundidad(Girar(Izquierda)) == 1)
  }

  test("profundidad propia: repetir una básica tiene profundidad 2") {
    assert(profundidad(Repetir(3, Avanzar(2))) == 2)
  }

  test("profundidad propia: dos repeticiones anidadas dan profundidad 3") {
    assert(profundidad(Repetir(2, Repetir(3, Avanzar(1)))) == 3)
  }

  test("profundidad propia: en una secuencia manda la rama más honda") {
    assert(profundidad(Secuencia(List(Avanzar(1), Repetir(2, Girar(Derecha))))) == 3)
  }

  test("profundidad propia: una secuencia que contiene una secuencia vacía tiene profundidad 2") {
    assert(profundidad(Secuencia(List(Secuencia(Nil)))) == 2)
  }

  test("profundidad propia: tres secuencias anidadas con una básica dan profundidad 4") {
    assert(profundidad(Secuencia(List(Secuencia(List(Secuencia(List(Avanzar(1)))))))) == 4)
  }


  // Punto 4: componer, rutina, aplicarA y transformar -------------------------

  test("punto 4 propio: componer conserva el orden de las órdenes") {
    assert(
      componer(Girar(Izquierda), Avanzar(3)) ==
        Secuencia(List(Girar(Izquierda), Avanzar(3)))
    )
  }

  test("punto 4 propio: rutina con cero repeticiones no mueve el robot") {
    assert(ejecutar(inicial, rutina(3)(Derecha)(0)) == inicial)
  }

  test("punto 4 propio: aplicarA conserva el resultado para cada estado") {
    assert(
      aplicarA(List(Estado(1, 2, Norte), Estado(4, 5, Este)), Avanzar(2)) ==
        List(Estado(1, 4, Norte), Estado(6, 5, Este))
    )
  }

  test("punto 4 propio: transformar modifica los giros y conserva los avances") {
    val resultado = transformar(cuadrado, {
      case Girar(Derecha) => Girar(Izquierda)
      case otra => otra
    })

    assert(
      resultado ==
        Repetir(4, Secuencia(List(Avanzar(1), Girar(Izquierda))))
    )
  }

  test("punto 4 propio: transformar conserva una secuencia vacía") {
    assert(transformar(Secuencia(Nil), (o: Orden) => o) == Secuencia(Nil))
  }

  // Punto 5: simplificar ------------------------------------------------------

  test("simplificar propia: un giro solo no cambia") {
    assert(simplificar(Girar(Izquierda)) == Girar(Izquierda))
  }

  test("simplificar propia: Avanzar(0) solo se vuelve la secuencia vacía") {
    assert(simplificar(Avanzar(0)) == Secuencia(Nil))
  }

  test("simplificar propia: una secuencia de un solo elemento es ese elemento") {
    assert(simplificar(Secuencia(List(Girar(Derecha)))) == Girar(Derecha))
  }

  test("simplificar propia: tres avances seguidos se juntan en uno") {
    assert(simplificar(Secuencia(List(Avanzar(1), Avanzar(2), Avanzar(3)))) == Avanzar(6))
  }

  test("simplificar propia: al quitar una orden vacía los avances vecinos se juntan") {
    val o = Secuencia(List(Avanzar(1), Repetir(0, Girar(Derecha)), Avanzar(2)))
    assert(simplificar(o) == Avanzar(3))
  }

  test("simplificar propia: repetir algo que no hace nada no hace nada") {
    assert(simplificar(Repetir(3, Secuencia(Nil))) == Secuencia(Nil))
  }

  test("simplificar propia: repetir un número negativo de veces se vuelve la secuencia vacía") {
    assert(simplificar(Repetir(-2, Avanzar(1))) == Secuencia(Nil))
  }

  test("simplificar propia: Repetir(1, ...) interior deja pasar la reducción a la secuencia de afuera") {
    val o = Secuencia(List(Avanzar(1), Repetir(1, Secuencia(List(Avanzar(0), Avanzar(4)))), Girar(Derecha)))
    assert(simplificar(o) == Secuencia(List(Avanzar(5), Girar(Derecha))))
  }

  test("simplificar propia: conserva las repeticiones y simplifica lo de adentro") {
    assert(
      simplificar(Repetir(3, Secuencia(List(Avanzar(1), Avanzar(1))))) ==
        Repetir(3, Avanzar(2))
    )
  }

  test("simplificar propia: una orden que ya está simplificada no cambia") {
    assert(simplificar(cuadrado) == cuadrado)
  }

  test("simplificar propia: secuencias anidadas de un avance se juntan") {
    val o = Secuencia(List(Secuencia(List(Avanzar(1))), Secuencia(List(Avanzar(2)))))
    assert(simplificar(o) == Avanzar(3))
  }

  test("simplificar propia: avances opuestos dentro de Repetir dejan solo el giro y el mismo estado") {
    val o = Repetir(2, Secuencia(List(Avanzar(3), Avanzar(-3), Girar(Izquierda))))
    assert(simplificar(o) == Repetir(2, Girar(Izquierda)))
    assert(ejecutar(inicial, simplificar(o)) == ejecutar(inicial, o))
  }
}
