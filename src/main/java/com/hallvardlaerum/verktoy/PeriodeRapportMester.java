package com.hallvardlaerum.verktoy;


import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.filerogopplasting.Filkyklop;
import com.hallvardlaerum.libs.filerogopplasting.StandardmappeEnum;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.kostnadspakke.PeriodedelAvKostnadspakkeRad;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

public class PeriodeRapportMester {
    private ArrayList<Periodepost> periodepostSortertArrayList;
    private ArrayList<PeriodedelAvKostnadspakkeRad> maanedsradkostnadspakkerArrayList;
    private static StyleBuilder boldStyle;
    private static StyleBuilder boldCenteredStyle;
    private static StyleBuilder columnTitleStyle;
    private static StyleBuilder smallTextStyle;
    private static String tittelString;
    private static String filnavnString;

    /**
     * Oppretter en rapport som viser perioden (Årsoversikt eller Månedsoversikt) med relevante periodeposter og kostnadspakker
    */
    public void lagrePeriodeSomPDF(
            Periode periode,
            ArrayList<Periodepost> periodepostSortertArrayList,
            ArrayList<PeriodedelAvKostnadspakkeRad> maanedsradkostnadspakkerArrayList)
    {
        this.periodepostSortertArrayList = periodepostSortertArrayList;
        this.maanedsradkostnadspakkerArrayList = maanedsradkostnadspakkerArrayList;

        opprettStiler();
        opprettTittelOgFilnavn(periode);

        try {
            report()
                    // Header
                    .title(
                            cmp.text(tittelString).setStyle(boldCenteredStyle),
                            cmp.verticalGap(20),
                            cmp.text(periode.getBeskrivelseString()),
                            cmp.verticalGap(20),
                            cmp.horizontalList()
                                    .add(
                                            opprettOppsummering_merkelapper(),
                                            opprettOppsummering_budsjettSummer(periode),
                                            opprettOppsummering_regnskapSummer(periode),
                                            opprettOppsummering_differanseSummer(periode),
                                            opprettOppsummering_inklOverfoeringSummer(periode)
                                    ),
                            cmp.verticalGap(20)
                    )
                    .summary(
                            cmp.subreport(opprettListePeriodeposter()),
                            cmp.verticalGap(40),
                            cmp.text(tittelString).setStyle(boldStyle),
                            cmp.verticalGap(20),
                            cmp.subreport(opprettListeKostnadspakker())
                    )
                    .pageFooter(
                            cmp.pageXofY(),
                            cmp.text(" Regnskapsprogrammet Blaahvalen. Periodeoversikt oppdatert " + periode.getRedigertDatoTid()).setStyle(smallTextStyle)
                    )
                    .toPdf(hentFileOutPutStream(filnavnString));

        } catch (DRException e) {
            throw new RuntimeException(e);
        }

    }

    public static void opprettDefaultFilnavn(){
        filnavnString = "Perioderapport.pdf";
        Filkyklop.hent().hentElleropprettFil(StandardmappeEnum.TEMP, filnavnString); //for å opprette filen hvis den ikke finnes ennå

    }

    // Denne sender filen til root for Blaahvalen
    private FileOutputStream hentFileOutPutStream(String simpeltfilnavnString){
        File fil = Filkyklop.hent().hentElleropprettFil(StandardmappeEnum.TEMP,simpeltfilnavnString);
        try {
            return new FileOutputStream(fil);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hentFilnavnString(){
        if (filnavnString==null) {
            opprettDefaultFilnavn();
        }
        return filnavnString;
    }

    private JasperReportBuilder opprettListePeriodeposter() {
        return report()
                .columns(
                        col.column("Kategori", "kategori", type.stringType()),
                        col.column("Budsjett", "sumBudsjett", type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
                        col.column("Regnskap", "sumRegnskap", type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
                        col.column("Beskrivelse", "beskrivelse", type.stringType())
                )
                .setColumnTitleStyle(columnTitleStyle)
                .setDataSource(lagRaderAvPerioder());
    }

    private JasperReportBuilder opprettListeKostnadspakker() {
        return report()
                .columns(
                        col.column("Kategori", "kategori", type.stringType()),
                        col.column("Kostnadspakke", "tittel", type.stringType()),
                        col.column("Beskrivelse", "beskrivelse", type.stringType()),
                        col.column("Sum hittil", "sumHittil", type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
                        col.column("Sum totalt", "sumTotalt", type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)

                )
                .setColumnTitleStyle(columnTitleStyle)
                .setDataSource(lagRaderAvKostnadspakker());
    }

    private JRDataSource lagRaderAvKostnadspakker() {
        DRDataSource dataSource = new DRDataSource("kategori", "tittel", "beskrivelse", "sumHittil", "sumTotalt");

        for (PeriodedelAvKostnadspakkeRad periodedelAvKostnadspakkeRad : maanedsradkostnadspakkerArrayList) {
            Periodepost kostnadspakke = periodedelAvKostnadspakkeRad.getKostnadspakke();
            if (kostnadspakke != null) {
                dataSource.add(
                        kostnadspakke.getKategori() != null ? kostnadspakke.getKategori().getTittel() : "",
                        kostnadspakke.getTittelString(),
                        kostnadspakke.getBeskrivelseString(),
                        HelTallMester.formaterIntegerSomStortTall(periodedelAvKostnadspakkeRad.getSumForDenneMaaned()),
                        HelTallMester.formaterIntegerSomStortTall(periodedelAvKostnadspakkeRad.getSumTotalt())
                );
            }
        }

        return dataSource;
    }


    private static VerticalListBuilder opprettOppsummering_inklOverfoeringSummer(Periode periode) {
        return cmp.verticalList()
                .add(
                        cmp.text("Med overføringer").setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
                        cmp.text(HelTallMester.formaterIntegerSomStortTall(periode.getSumRegnskapInntektMedOverfoeringerInteger())),
                        cmp.text(HelTallMester.formaterIntegerSomStortTall(periode.getSumRegnskapUtgifterMedOverfoeringerInteger())),
                        cmp.text(HelTallMester.formaterIntegerSomStortTall(periode.getSumRegnskapResultatMedOverfoeringerInteger()))
                )
                .setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT));
    }

    private static VerticalListBuilder opprettOppsummering_differanseSummer(Periode periode) {
        return cmp.verticalList()
                .add(
                        cmp.text("Differanse").setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
                        cmp.text(HelTallMester.formaterIntegerSomStortTall(periode.getSumDifferanseBudsjettRegnskapInntekter())),
                        cmp.text(HelTallMester.formaterIntegerSomStortTall(periode.getSumDifferanseBudsjettRegnskapUtgifter())),
                        cmp.text(HelTallMester.formaterIntegerSomStortTall(periode.getSumDifferanseBudsjettRegnskapResultat())).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)
                )
                .setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT));
    }

    private static VerticalListBuilder opprettOppsummering_regnskapSummer(Periode periode) {
        return cmp.verticalList()
                .add(
                        cmp.text("Regnskap").setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
                        cmp.text(HelTallMester.formaterIntegerSomStortTall(periode.getSumRegnskapInntektInteger())).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
                        cmp.text(HelTallMester.formaterIntegerSomStortTall(periode.getSumRegnskapUtgifterInteger())).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
                        cmp.text(HelTallMester.formaterIntegerSomStortTall(periode.getSumRegnskapResultatInteger())).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)
                )
                .setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT));
    }

    private static VerticalListBuilder opprettOppsummering_budsjettSummer(Periode periode) {
        return cmp.verticalList()
                .add(
                        cmp.text("Budsjett").setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
                        cmp.text(HelTallMester.formaterIntegerSomStortTall(periode.getSumBudsjettInntektInteger())).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
                        cmp.text(HelTallMester.formaterIntegerSomStortTall(periode.getSumBudsjettUtgifterInteger())).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
                        cmp.text(HelTallMester.formaterIntegerSomStortTall(periode.getSumBudsjettResultatInteger())).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)
                )
                .setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT));
    }

    private static VerticalListBuilder opprettOppsummering_merkelapper() {
        return cmp.verticalList()
                .add(
                        cmp.text(" "),
                        cmp.text("Inn"),
                        cmp.text("Ut"),
                        cmp.text("Resultat")
                )
                .setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT));
    }

    private void opprettTittelOgFilnavn(Periode periode) {
        if (periode.getPeriodetypeEnum() == PeriodetypeEnum.MAANEDSOVERSIKT) {
            tittelString = "Månedoversikt " + Datokyklop.hent().formaterLocalDate_YYYY_MM(periode.getDatoFraLocalDate());
        } else if (periode.getPeriodetypeEnum() == PeriodetypeEnum.AARSOVERSIKT) {
            tittelString = "Årsoversikt " + Datokyklop.hent().formaterLocalDate_YYYY(periode.getDatoFraLocalDate());
        } else {
            tittelString = "Ukjent periodetype " + periode.getDatoFraLocalDate();
        }
        opprettDefaultFilnavn();
    }

    private void opprettStiler() {
        boldStyle = stl.style().bold();
        boldCenteredStyle = stl
                .style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        columnTitleStyle = stl.style(boldCenteredStyle)
                .setBorder(stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);
        smallTextStyle = stl.style().setFontSize(6);
    }

    private JRDataSource lagRaderAvPerioder() {
        DRDataSource dataSource = new DRDataSource("kategori", "sumBudsjett", "sumRegnskap", "beskrivelse");

        for (Periodepost periodepost : periodepostSortertArrayList) {
            dataSource.add(
                    periodepost.getKategori() != null ? periodepost.getKategori().getTittel() : "",
                    HelTallMester.formaterIntegerSomStortTall(periodepost.getSumBudsjettInteger()),
                    HelTallMester.formaterIntegerSomStortTall(periodepost.getSumRegnskapInteger()),
                    periodepost.getBeskrivelseString());
        }

        return dataSource;
    }

    public static void printTestPDF() {
        try {
            // Create report columns
            report()
                    // Add columns
                    .columns(
                            Columns.column("Name", "name", DataTypes.stringType()),
                            Columns.column("Age", "age", DataTypes.integerType()),
                            Columns.column("City", "city", DataTypes.stringType())
                    )
                    // Set title
                    .title(cmp.text("Employee Report")
                                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                                    .setStyle(
                                            stl.style()
                                                    .setBold(true)
                                                    .setFontSize(18)
                                    ),
                            cmp.verticalGap(20),
                            cmp.horizontalList()
                                    .add(
                                            cmp.verticalList()
                                                    // First column
                                                    .add(
                                                            cmp.text("Column 1 Content a"),
                                                            cmp.text("Column 1 Content b"),
                                                            cmp.text("Column 1 Content c")
                                                    )
                                                    .setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)),

                                            // Second column
                                            cmp.text("Column 2 Content")
                                                    .setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)),

                                            // Third column
                                            cmp.text("Column 3 Content")
                                                    .setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT))
                                    ),
                            cmp.verticalGap(20)
                    )

                    .columnHeader(
                            cmp.line()
                                    .setPen(stl.pen1Point())
                    )
                    // Use a data source
                    .setDataSource(createDataSource())

                    // Export to PDF
                    .toPdf(new FileOutputStream("employee_report.pdf"));
        } catch (DRException | FileNotFoundException e) {
            Loggekyklop.bruk().loggFEIL("Feil ved opprettelse av pdf-filen for utskrift av perioderapport.");
        }
    }

    // Create a sample data source
    private static JRDataSource createDataSource() {
        DRDataSource dataSource = new DRDataSource("name", "age", "city");
        dataSource.add("John Doe", 35, "New York");
        dataSource.add("Jane Smith", 28, "San Francisco");
        dataSource.add("Bob Johnson", 42, "Chicago");
        return dataSource;
    }

// Info om struktur i rapporter:
// * private void build() {
// *      try {
// *        report()//create new report design
// *          .columns(...) //adds columns
// *          .groupBy(...) //adds groups
// *          .subtotalsAtSummary(...) //adds subtotals
// *          ...
// *          //set datasource
// *          .setDataSource(...)
// *          //export report
// *          .toPdf(...) //export report to pdf
// *          .toXls(...) //export report to excel
// *          ...
// *          //other outputs
// *          .toJasperPrint() //creates jasperprint object
// *          .show() //shows report
// *          .print() //prints report
// *          ...
// *      } catch (DRException e) {
// *        e.printStackTrace();
// *      }
// *    }
// */

}
