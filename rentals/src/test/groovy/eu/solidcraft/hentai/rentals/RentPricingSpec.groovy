package eu.solidcraft.hentai.rentals
import eu.solidcraft.hentai.infrastructure.TimeService
import eu.solidcraft.hentai.rentals.price.FilmType
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

class RentPricingSpec extends Specification {
    BigDecimal premiumPrice = 40
    BigDecimal basePrice = 30
    RentPriceCalculator priceCalculator

    def setup() {
        priceCalculator = new RentPriceCalculator(premiumPrice, basePrice)
    }

    @Unroll
    def "for filmType #filmType and numberOfDays #numberOfDays, calculated price should be #price"() {
        expect:
            priceCalculator.calculatePrice(filmType, numberOfDays) == price

        where:
            filmType             | numberOfDays | price
            FilmType.NEW_RELEASE | 1            | 40
            FilmType.REGULAR     | 5            | 90
            FilmType.REGULAR     | 2            | 30
            FilmType.OLD         | 7            | 90
    }

    @Unroll
    def "returning a filmType #filmType, #additionlDaysOfRent after due date, should result in #price surcharge"() {
        given:
            Long filmId = 1
            LocalDate rentedOn = TimeService.now()
            Rent rent = new Rent(filmId, filmType, initialNumberOfDaysForRent, "Seba", rentedOn, priceCalculator)
        when:
            rent.returned(rentedOn.plusDays(initialNumberOfDaysForRent + additionlDaysOfRent), priceCalculator)
        then:
            rent.lateReturnSurgcharge == price

        where:
            filmType             | initialNumberOfDaysForRent | additionlDaysOfRent | price
            FilmType.NEW_RELEASE | 5                          | 2                   | 80
            FilmType.REGULAR     | 3                          | 1                   | 30
    }


}
