package com.hallvardlaerum.verktoy;


import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.Periodepost;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;


import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * private void build() {
 *      try {
 *        report()//create new report design
 *          .columns(...) //adds columns
 *          .groupBy(...) //adds groups
 *          .subtotalsAtSummary(...) //adds subtotals
 *          ...
 *          //set datasource
 *          .setDataSource(...)
 *          //export report
 *          .toPdf(...) //export report to pdf
 *          .toXls(...) //export report to excel
 *          ...
 *          //other outputs
 *          .toJasperPrint() //creates jasperprint object
 *          .show() //shows report
 *          .print() //prints report
 *          ...
 *      } catch (DRException e) {
 *        e.printStackTrace();
 *      }
 *    }
 */

import static net.sf.dynamicreports.report.builder.DynamicReports.*;
public class PeriodeRapportMester {
    private Periode periode;
    private ArrayList<Periodepost> periodepostSortertArrayList;

    public void lagrePeriodeSomPDF(Periode periode, ArrayList<Periodepost> periodepostSortertArrayList) {
        this.periode = periode;
        this.periodepostSortertArrayList = periodepostSortertArrayList;

        StyleBuilder boldStyle = stl.style().bold();
        StyleBuilder boldCenteredStyle = stl
                .style(boldStyle)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        StyleBuilder columnTitleStyle  = stl.style(boldCenteredStyle)
                .setBorder(stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);

        String tittelString;
        if (periode.getPeriodetypeEnum()== PeriodetypeEnum.MAANEDSOVERSIKT) {
            tittelString = "Månedoversikt " + Datokyklop.hent().formaterLocalDate_YYYY_MM(periode.getDatoFraLocalDate());
        } else if (periode.getPeriodetypeEnum() == PeriodetypeEnum.AARSOVERSIKT) {
            tittelString = "Årsoversikt " + Datokyklop.hent().formaterLocalDate_YYYY(periode.getDatoFraLocalDate());
        } else {
            tittelString = "Ukjent periodetype " + periode.getDatoFraLocalDate();
        }

        String filnavnString = "ukjent.pdf";
        if (periode.getPeriodetypeEnum() == PeriodetypeEnum.MAANEDSOVERSIKT) {
            filnavnString = "Maanedsoversikt_" +
                    Datokyklop.hent().formaterLocalDate_YYYY_MM(periode.getDatoFraLocalDate());
        } else if (periode.getPeriodetypeEnum() == PeriodetypeEnum.AARSOVERSIKT) {
            filnavnString = "Aarsoversikt_" +
                    Datokyklop.hent().formaterLocalDate_YYYY(periode.getDatoFraLocalDate());
        }

        try {
            report()
                .title(
                    cmp.text(tittelString).setStyle(boldCenteredStyle),
                    cmp.verticalGap(20),
                    cmp.text(periode.getBeskrivelseString()),
                    cmp.verticalGap(20),
                    cmp.horizontalList()
                            .add(
                                cmp.verticalList()
                                    .add(
                                        cmp.text(" "),
                                        cmp.text("Inn"),
                                        cmp.text("Ut"),
                                        cmp.text("Resultat")
                                    )
                                    .setStyle(stl.style().setHorizontalAlignment(HorizontalAlignment.RIGHT)),
                                cmp.verticalList()
                                        .add(
                                            cmp.text("Budsjett"),
                                            cmp.text(HelTallMester.integerFormatertSomStortTall(periode.getSumBudsjettInntektInteger())),
                                            cmp.text(HelTallMester.integerFormatertSomStortTall(periode.getSumBudsjettUtgifterInteger())),
                                            cmp.text(HelTallMester.integerFormatertSomStortTall(periode.getSumBudsjettResultatInteger()))
                                        )
                                        .setStyle(stl.style().setHorizontalAlignment(HorizontalAlignment.RIGHT)),

                                cmp.verticalList()
                                        .add(
                                            cmp.text("Regnskap"),
                                            cmp.text(HelTallMester.integerFormatertSomStortTall(periode.getSumRegnskapInntektInteger())),
                                            cmp.text(HelTallMester.integerFormatertSomStortTall(periode.getSumRegnskapUtgifterInteger())),
                                            cmp.text(HelTallMester.integerFormatertSomStortTall(periode.getSumRegnskapResultatInteger()))
                                        )
                                        .setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)),

                                cmp.verticalList()
                                        .add(
                                            cmp.text("Differanse"),
                                            cmp.text(" "),
                                            cmp.text(" "),
                                            cmp.text(HelTallMester.integerFormatertSomStortTall(periode.getSumDifferanseResultatBudsjettRegnskap()))
                                        )
                                        .setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)),

                                cmp.verticalList()
                                        .add(
                                            cmp.text("Med overføringer"),
                                            cmp.text(HelTallMester.integerFormatertSomStortTall(periode.getSumRegnskapInntektMedOverfoeringerInteger())),
                                            cmp.text(HelTallMester.integerFormatertSomStortTall(periode.getSumRegnskapUtgifterMedOverfoeringerInteger())),
                                            cmp.text(HelTallMester.integerFormatertSomStortTall(periode.getSumRegnskapResultatMedOverfoeringerInteger()))
                                        )
                                        .setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT))
                            ),
                    cmp.verticalGap(20)
                )

                .columns(
                    col.column("Kategori","kategori",type.stringType()),
                    col.column("Budsjett","sumBudsjett",type.stringType()),
                    col.column("Regnskap","sumRegnskap",type.stringType()),
                    col.column("Beskrivelse","beskrivelse",type.stringType())
                )
                .setColumnTitleStyle(columnTitleStyle)

                .pageFooter(
                        cmp.pageXofY(),
                        cmp.text(" Regnskapsprogrammet Blaahvalen. Periodeoversikt oppdatert " + periode.getRedigertDatoTid())
                )

                .setDataSource(lagRaderAvPerioder())

                .toPdf(new FileOutputStream(filnavnString));

        } catch (DRException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ;
    }

    private JRDataSource lagRaderAvPerioder() {
        DRDataSource dataSource = new DRDataSource("kategori", "sumBudsjett", "sumRegnskap","beskrivelse");

        for (Periodepost periodepost:periodepostSortertArrayList) {
            dataSource.add(
                periodepost.getKategori().getTittel(),
                HelTallMester.integerFormatertSomStortTall(periodepost.getSumBudsjettInteger()),
                HelTallMester.integerFormatertSomStortTall(periodepost.getSumRegnskapInteger()),
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
            e.printStackTrace();
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


}
