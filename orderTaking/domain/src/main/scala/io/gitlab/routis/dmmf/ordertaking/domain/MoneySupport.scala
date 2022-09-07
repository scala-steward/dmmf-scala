package io.gitlab.routis.dmmf.ordertaking.domain

import zio.prelude.Validation

import scala.annotation.targetName
import scala.util.Try

object MoneySupport:

  import org.joda.money.Money as JodaMoney

  opaque type Money = JodaMoney
  object Money:
    import org.joda.money.CurrencyUnit as JodaCurrencyUnit

    private val EUR: JodaCurrencyUnit = JodaCurrencyUnit.EUR

    def apply(amount: BigDecimal): Money = JodaMoney.of(EUR, amount.bigDecimal)

    def make(amount: BigDecimal): Validation[String, Money] =
      Validation
        .fromTry(Try(Money(amount)))
        .mapError(t => s"Not a valid amount. ${t.getMessage}")

    val zero: Money = Money(0)

    extension (money: Money)
      @targetName("+")
      def +(other: Money): Money = money.plus(other)
      @targetName("*")
      def *(times: Long): Money = money.multipliedBy(times)

    given MoneyAdditionIsIdentity: zio.prelude.Identity[Money] with
      override def identity: Money = zero

      override def combine(l: => Money, r: => Money): Money = l + r

    given MoneyHasOrdering: Ordering[Money] with
      override def compare(x: Money, y: Money): Int = x.compareTo(y)