// The MIT License (MIT)
// Copyright (c) 2016 Mateusz Pawlik and Nikolaus Augsten
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy 
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights 
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
// copies of the Software, and to permit persons to whom the Software is 
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

// package test;

import util.LblTree;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import distance.APTED;

/**
 * Correctness unit tests.
 *
 * @author Nikolaus Augsten, Mateusz Pawlik
 *
 */
public class CorrectnessTest {

	private APTED ted;
	
	@Before
	public void init() {
		ted= new APTED((float)1.0, (float)1.0, (float)1.0);
	}
		
	private void symmetricTest(String strT1, String strT2, double expected) {
		LblTree t1, t2;
		double result;
		t1 = LblTree.fromString(strT1);
		t2 = LblTree.fromString(strT2);
		result = ted.nonNormalizedTreeDist(t1, t2);
		assertEquals(expected, result, 0.000000001);
		result = ted.nonNormalizedTreeDist(t2, t1);
		assertEquals(expected, result, 0.000000001);		
	}
	
	private void nonSymmetricTest(String strT1, String strT2, double expected) {
		LblTree t1, t2;
		double result;
		t1 = LblTree.fromString(strT1);
		t2 = LblTree.fromString(strT2);
		result = ted.nonNormalizedTreeDist(t1, t2);
		assertEquals(expected, result, 0.000000001);		
	}

	@Test
  public void unexpectedErrorsTest() {
  	nonSymmetricTest("{2{6}{8{5{3{6}{3}{2}}}}{6}}", "{1{2}{0}}", 8.0);
  	nonSymmetricTest("{8{9{8{5}{9{6}{9}{7}}{0{6}}}{0}}}", "{1{8}{4{4}{0{3{3{3}{0}}}}}}", 14.0);
  	nonSymmetricTest("{1{1}}", "{c{a{b{4{b}{0{3}}{0{c}{9}}}}{1}{a}}{7}{d}}", 13.0);
  	nonSymmetricTest("{1{2{2{5}{7{6{7}}}}}{0}}", "{0{2}{1}{1}}", 8.0);
  	nonSymmetricTest("{7{1{3{c{j{c}{j}}{2{d}}{8{5}{a}{3}}}}}{m{2{c{e}{4}{b{h}{f}{k}}}}}{d}}", "{1{1}}", 22.0);
  	nonSymmetricTest("{1{1}{2}}", "{3{8{4{3}{9{0}{4}{7}}}}{7}{8}}", 10.0);
  	nonSymmetricTest("{n{h{q{n{3{n}{f}{o}}}{t{q}{o}{10{k}{11}}}{d}}{j{8}{p{v{2}{8}{3}}}{12}}{2}}{z}{12{12}{r{p{h{b}{o}{11}}{m}{t{o}{6}{5}}}}{k}}}", "{8{0{c{6{1}{g{8}{c}{d}}}{2{6}{8{f}{9}}}}{e}}{a}{a}}", 40.0);
  	nonSymmetricTest("{1{3}{5{2{6}{5}{5}}}{5}}", "{0{3}{2}{1}}", 6.0);
  	nonSymmetricTest("{3{8{n{q}{3{i{r}}}}{p{n{4}{n{s}{l}}}}}{n{3{e{h}{g}{m{j}{6}}}}{a}{r}}{2{p{j{n{f}}}}{n}}}", "{0{1}}", 29.0);
  	nonSymmetricTest(
  			"{3i{4q{88}{3y}{n{3v}{32}{s{f{3a{6m{7r{6m{78}{3j}{3i}{5t}{6g}}{55{1f}{2u}{1i}{2b}}{21{p}{72}}{2o}{18}}{39{3e}{6z}{8c{4t}{4n}{1h}{14}{3u}}{2s}}}{r}{33}{3o}{13{g}{2z{5o{1u}{g}{3u}{59}{12}{1a}}{6z{2x}{15}}{21{3w}{6l}{6x}{5j}{5n}{29}}{7g}{3y}}}{4l}}{68{59{5t{2f{31}{14}{2f}{26}{34}{5e}}{4c{32}{78}{6p}{6c}{6w}{25}}{7c{2j}{7q}{6t}{1h}{85}}}}{75}{85}}{1x{6d{19{6w}{u{11}{68}{5z}{73}{20}{0}}{3w}{6c}}{1l}{61}{2f{1x{3f}{7o}{77}{40}{5h}}{5y{3f}{3o}{7u}{3p}{8b}}{25}}{1y}{62}}{7o}{1u{1v{n}{87}{e{m}{1q}{2u}{88}}{73{6f}}}}}}{57{55{4w}{1{3q}{29{18{g}{7f}{2z}{4i}}}}{87{6f}{1j}{5q}{2n{8b{34}{1e}}{3d}{7{3b}{5d}{k}}{6i}{5j{7}{8}{p}{4r}{1n}}}}{3y}{61}{16{7r{4v}{2s{4c}{62}{4c}{47}{7f}}{42}{8e}}}}}{54{6j}{3p}{2x}{60}{60{1g{7j{2d}{5r{4i}{31}{p}{59}{37}{5z}}{7v}}{5e}{38}{5c{4f}{2g}{17}{4y{i}{1v}{2k}{1z}{87}}}{88}{50{4x{11}{4c}{25}{7e}{6z}{10}}}}}}}{5y}{4r{b}{56}{4b{7q{5g{1z}{2v{6o{5g}{6t}}{43}}{5f}{66{6e{6b}{1u}{1i}}{6f{4k}{87}{q}}{2n}{5x{65}{6c}}{4l}}}{1{7f{4h}{s{43}{2i}{5s}}{4t}{7l{y}{4z}{76}{8a}{83}}{2m}}}}{7c}{5g}{55{5p{t{2o}{7z{29}{78}{50}}}{2z}{7n}{6p{4o{55}{8d}{9}{7}{5a}{5j}}{6y}}}{1t{3s}{1n{5l{6y}{h}{61}{3d}{7x}{89}}{7q{b}}}{47{4{40}{1p}{7u}{7d}}{45{m}{2l}{1e}{3}{6c}}}{6x}}}}}}}}", 
  			"{5j{p{3o{6h{5h{b{46}{3j{7y}{6f}{o}{60}}{2k}{3t{70}{4}{2t}{g}{3o}{5e}}}}{3d}{5f{4t{b}{4j{40}{4d}}{2v}}}{5t{48{6j{y}}}{9{2f}{b{32}{5r}{6y}{8}{z}{3}}{3}{7x{2v}{16}{5}}}{52}{2c}{1q{17{41}{1b}{6y}}{34}}{31}}{4x}{78}}}}{27}{d{6z{6i}{5d{5f{6r}{1p{43{11}{1b}{6k}{26}}{5s}{2w}{3b}{48}{3m{3f}{3d}{4j}{13}{k}{5l}}}{5v}}{38}}}{39{6c}{14}{1o{3r{2u{5n}{40{h}}{7t}}{2l{3b{5c}}{6i}{p}{60}{1p{38}{46}}{1g}}{4g}{1e}}{27}}{5z}{5m{59}{62{2j{3y{l}}{7b{7n}{7w}{b}{2d}{28}}{3e{29}{1a}{6k}{20}{4e}{41}}{35}{4b}}}{1c}}}{1h{63}{2v{2e}{4q{2o}{32}{30{2x}{7z}{6l{3z}{5e}{1q}{6r}}{0}{i{4f}{3k}}{3z{6t}}}{75}{3q}}}}}{37}{g{5j}{30{5h}{6o}{1n}{7i{41}{z{6l}{5k{5x{5e}{45}{2o}{58}{71}{33}}}{h}}{3j{49{16}{28{68}{u}{2u}{7t}{17}{42}}}}{33}}{4k}{8}}{7s}{2n{4w{6z{4e}{54{4a{63}{4j}{6l}{11}}{6c}{3t}{1d{4k}{67}}}{21}{3g{2e{4x}{z}{2z}}{5z{1b}{26}{1t}{o}}{6}}}}}{1v{17}{77{2b{3o}{s}{4v{53{3u}}}{10{1t}{7s{64}{b}{59}{3m}{f}}}}}{3v}{3y}{2i{2t}{22{4y{4p{51}{1x}{p}{2j}}{5x}}{2q}{5v}}{56{1a}{5j}{4l}{1w}{29{5p{7y}{3j}{1z}}{49{7o}{3d}{63}{18}}{7v{41}{2w}}}}{3f{42{50{78}}}{2b}{6o{47}{71}{7z}{3a}{6j}{1k{1a}{5n}{7v}}}{7d{n{4e}}}{4o{7u}{5j{4y}{47}{29}{57}}}{61}}}}{7q}}}", 
  			373.0);
  	nonSymmetricTest(
  			"{Entry{class{STANDARD}}{id{AGRI_CHICK}}{mtype{PRT}}{seqlen{1955}}{AC{P31696}}{Mod{Rel{26}}{date{01-JUL-1993}}{type{Created}}}{Mod{Rel{26}}{date{01-JUL-1993}}{type{Last sequence update}}}{Mod{Rel{39}}{date{30-MAY-2000}}{type{Last annotation update}}}{Descr{AGRIN PRECURSOR}}{Gene{AGRN}}{Species{Gallus gallus (Chicken)}}{Org{Eukaryota}}{Org{Metazoa}}{Org{Chordata}}{Org{Craniata}}{Org{Vertebrata}}{Org{Euteleostomi}}{Org{Archosauria}}{Org{Aves}}{Org{Neognathae}}{Org{Galliformes}}{Org{Phasianidae}}{Org{Phasianinae}}{Org{Gallus}}{Ref{num{1}}{pos{SEQUENCE FROM N.A}}{Comment{TISSUE=BRAIN}}{DB{MEDLINE}}{MedlineID{92232297}}{Author{Tsim K.W.K}}{Author{Ruegg M.A}}{Author{Escher G}}{Author{Kroeger S}}{Author{McMahan U.J}}{Cite{Neuron 8;677-689(1992)}}}{Ref{num{2}}{pos{ALTERNATIVE SPLICING}}{DB{MEDLINE}}{MedlineID{92232298}}{Author{Ruegg M.A}}{Author{Tsim K.W.K}}{Author{Horton S.E}}{Author{Kroeger S}}{Author{Escher G}}{Author{Gensch E.M}}{Author{McMahan U.J}}{Cite{Neuron 8;691-699(1992)}}}{EMBL{prim_id{M94271}}{sec_id{AAA48585}}}{EMBL{prim_id{M97371}}{sec_id{AAA48586}}}{EMBL{prim_id{M97372}}{sec_id{-}}{status{NOT_ANNOTATED_CDS}}}{PIR{prim_id{JH0591}}{sec_id{AGCH}}}{INTERPRO{prim_id{IPR000082}}{sec_id{-}}}{INTERPRO{prim_id{IPR000152}}{sec_id{-}}}{INTERPRO{prim_id{IPR000561}}{sec_id{-}}}{INTERPRO{prim_id{IPR001239}}{sec_id{-}}}{INTERPRO{prim_id{IPR001791}}{sec_id{-}}}{INTERPRO{prim_id{IPR002049}}{sec_id{-}}}{INTERPRO{prim_id{IPR002350}}{sec_id{-}}}{PFAM{prim_id{PF00008}}{sec_id{EGF}}{status{4}}}{PFAM{prim_id{PF01390}}{sec_id{SEA}}{status{1}}}{PFAM{prim_id{PF00050}}{sec_id{kazal}}{status{9}}}{PFAM{prim_id{PF00053}}{sec_id{laminin_EGF}}{status{2}}}{PFAM{prim_id{PF00054}}{sec_id{laminin_G}}{status{3}}}{PRINTS{prim_id{PR00290}}{sec_id{KAZALINHBTR}}}{PROSITE{prim_id{PS00010}}{sec_id{ASX_HYDROXYL}}{status{1}}}{PROSITE{prim_id{PS00022}}{sec_id{EGF_1}}{status{6}}}{PROSITE{prim_id{PS01186}}{sec_id{EGF_2}}{status{1}}}{PROSITE{prim_id{PS01248}}{sec_id{LAMININ_TYPE_EGF}}{status{1}}}{Keyword{Glycoprotein}}{Keyword{EGF-like domain}}{Keyword{Repeat}}{Keyword{Alternative splicing}}{Keyword{Signal}}{Keyword{Laminin EGF-like domain}}{Features{SIGNAL{from{1}}{to{38}}{Descr{POTENTIAL}}}{CHAIN{from{39}}{to{1955}}{Descr{AGRIN}}}{DOMAIN{from{54}}{to{126}}{Descr{KAZAL-LIKE 1}}}{DOMAIN{from{130}}{to{201}}{Descr{KAZAL-LIKE 2}}}{DOMAIN{from{202}}{to{273}}{Descr{KAZAL-LIKE 3}}}{DOMAIN{from{276}}{to{344}}{Descr{KAZAL-LIKE 4}}}{DOMAIN{from{350}}{to{418}}{Descr{KAZAL-LIKE 5}}}{DOMAIN{from{419}}{to{483}}{Descr{KAZAL-LIKE 6}}}{DOMAIN{from{484}}{to{548}}{Descr{KAZAL-LIKE 7}}}{DOMAIN{from{551}}{to{633}}{Descr{KAZAL-LIKE 8}}}{DOMAIN{from{675}}{to{728}}{Descr{LAMININ EGF-LIKE 1}}}{DOMAIN{from{729}}{to{775}}{Descr{LAMININ EGF-LIKE 2}}}{DOMAIN{from{781}}{to{851}}{Descr{KAZAL-LIKE}}}{DOMAIN{from{1229}}{to{1265}}{Descr{EGF-LIKE 1}}}{DOMAIN{from{1446}}{to{1483}}{Descr{EGF-LIKE 2}}}{DOMAIN{from{1485}}{to{1522}}{Descr{EGF-LIKE 3}}}{DOMAIN{from{1714}}{to{1752}}{Descr{EGF-LIKE 4}}}{DOMAIN{from{856}}{to{995}}{Descr{SER/THR-RICH}}}{DOMAIN{from{1150}}{to{1219}}{Descr{SER/THR-RICH}}}{DISULFID{from{86}}{to{105}}{Descr{POTENTIAL}}}{DISULFID{from{94}}{to{126}}{Descr{POTENTIAL}}}{DISULFID{from{160}}{to{180}}{Descr{POTENTIAL}}}{DISULFID{from{169}}{to{201}}{Descr{POTENTIAL}}}{DISULFID{from{233}}{to{252}}{Descr{POTENTIAL}}}{DISULFID{from{241}}{to{273}}{Descr{POTENTIAL}}}{DISULFID{from{304}}{to{323}}{Descr{POTENTIAL}}}{DISULFID{from{312}}{to{344}}{Descr{POTENTIAL}}}{DISULFID{from{378}}{to{397}}{Descr{POTENTIAL}}}{DISULFID{from{386}}{to{418}}{Descr{POTENTIAL}}}{DISULFID{from{443}}{to{462}}{Descr{POTENTIAL}}}{DISULFID{from{451}}{to{483}}{Descr{POTENTIAL}}}{DISULFID{from{507}}{to{527}}{Descr{POTENTIAL}}}{DISULFID{from{516}}{to{548}}{Descr{POTENTIAL}}}{DISULFID{from{592}}{to{612}}{Descr{POTENTIAL}}}{DISULFID{from{601}}{to{633}}{Descr{POTENTIAL}}}{DISULFID{from{675}}{to{687}}{Descr{BY SIMILARITY}}}{DISULFID{from{677}}{to{694}}{Descr{BY SIMILARITY}}}{DISULFID{from{696}}{to{705}}{Descr{BY SIMILARITY}}}{DISULFID{from{708}}{to{726}}{Descr{BY SIMILARITY}}}{DISULFID{from{729}}{to{741}}{Descr{BY SIMILARITY}}}{DISULFID{from{731}}{to{748}}{Descr{BY SIMILARITY}}}{DISULFID{from{750}}{to{759}}{Descr{BY SIMILARITY}}}{DISULFID{from{762}}{to{773}}{Descr{BY SIMILARITY}}}{DISULFID{from{810}}{to{830}}{Descr{POTENTIAL}}}{DISULFID{from{819}}{to{851}}{Descr{POTENTIAL}}}{DISULFID{from{1233}}{to{1244}}{Descr{BY SIMILARITY}}}{DISULFID{from{1238}}{to{1253}}{Descr{BY SIMILARITY}}}{DISULFID{from{1255}}{to{1264}}{Descr{BY SIMILARITY}}}{DISULFID{from{1450}}{to{1461}}{Descr{BY SIMILARITY}}}{DISULFID{from{1455}}{to{1471}}{Descr{BY SIMILARITY}}}{DISULFID{from{1473}}{to{1482}}{Descr{BY SIMILARITY}}}{DISULFID{from{1489}}{to{1500}}{Descr{BY SIMILARITY}}}{DISULFID{from{1494}}{to{1510}}{Descr{BY SIMILARITY}}}{DISULFID{from{1512}}{to{1521}}{Descr{BY SIMILARITY}}}{DISULFID{from{1718}}{to{1731}}{Descr{BY SIMILARITY}}}{DISULFID{from{1725}}{to{1740}}{Descr{BY SIMILARITY}}}{DISULFID{from{1742}}{to{1751}}{Descr{BY SIMILARITY}}}{CARBOHYD{from{390}}{to{390}}{Descr{N-LINKED (GLCNAC...) (POTENTIAL)}}}{CARBOHYD{from{659}}{to{659}}{Descr{N-LINKED (GLCNAC...) (POTENTIAL)}}}{CARBOHYD{from{764}}{to{764}}{Descr{N-LINKED (GLCNAC...) (POTENTIAL)}}}{CARBOHYD{from{814}}{to{814}}{Descr{N-LINKED (GLCNAC...) (POTENTIAL)}}}{VARSPLIC{from{1648}}{to{1651}}{Descr{MISSING (IN AGRIN-RELATED PROTEIN 2)}}}{VARSPLIC{from{1783}}{to{1793}}{Descr{MISSING (IN AGRIN-RELATED PROTEIN 1 AND}}{Descr{AGRIN-RELATED PROTEIN 2)}}}{CONFLICT{from{1129}}{to{1131}}{Descr{RTI -> SIL (IN AAA48586)}}}}}", 
  			"{Entry{class{STANDARD}}{id{PSBL_ORYSA}}{mtype{PRT}}{seqlen{37}}{AC{P12166}}{AC{P12167}}{AC{Q34007}}{Mod{Rel{12}}{date{01-OCT-1989}}{type{Created}}}{Mod{Rel{37}}{date{15-DEC-1998}}{type{Last sequence update}}}{Mod{Rel{37}}{date{15-DEC-1998}}{type{Last annotation update}}}{Descr{PHOTOSYSTEM II REACTION CENTER L PROTEIN (PSII 5 KDA PROTEIN)}}{Gene{PSBL}}{Species{Oryza sativa (Rice), Nicotiana tabacum (Common tobacco)}}{Species{Hordeum vulgare (Barley), Triticum aestivum (Wheat)}}{Species{Secale cereale (Rye), Zea mays (Maize), Pisum sativum (Garden pea)}}{Species{Spinacia oleracea (Spinach), Capsicum annuum (Bell pepper)}}{Species{Mesembryanthemum crystallinum (Common ice plant)}}{Species{Beta vulgaris (Sugar beet), and Populus deltoides (Poplar)}}{Organelle{Chloroplast}}{Org{Eukaryota}}{Org{Viridiplantae}}{Org{Embryophyta}}{Org{Tracheophyta}}{Org{Spermatophyta}}{Org{Magnoliophyta}}{Org{Liliopsida}}{Org{Poales}}{Org{Poaceae}}{Org{Oryza}}{Ref{num{1}}{pos{SEQUENCE FROM N.A}}{Comment{SPECIES=O.SATIVA}}{Comment{STRAIN=CV. NIPPONBARE}}{Author{Sugiura M}}{Cite{Submitted (JUL-1989) to the EMBL/GenBank/DDBJ databases}}}{Ref{num{2}}{pos{COMPLETE GENOME}}{Comment{SPECIES=O.SATIVA}}{DB{MEDLINE}}{MedlineID{89364698}}{Author{Hiratsuka J}}{Author{Shimada H}}{Author{Whittier R}}{Author{Ishibashi T}}{Author{Sakamoto M}}{Author{Mori M}}{Author{Kondo C}}{Author{Honji Y}}{Author{Sun C.-R}}{Author{Meng B.-Y}}{Author{Li Y.-Q}}{Author{Kanno A}}{Author{Nishizawa Y}}{Author{Hirai A}}{Author{Shinozaki K}}{Author{Sugiura M}}{Cite{Mol. Gen. Genet. 217;185-194(1989)}}}{Ref{num{3}}{pos{SEQUENCE FROM N.A}}{Comment{SPECIES=N.TABACUM}}{Comment{STRAIN=CV. BRIGHT YELLOW 4}}{Author{Sugiura M}}{Cite{Submitted (AUG-1986) to the EMBL/GenBank/DDBJ databases}}}{Ref{num{4}}{pos{COMPLETE GENOME}}{Comment{SPECIES=N.TABACUM}}{Author{Shinozaki K}}{Author{Ohme M}}{Author{Tanaka M}}{Author{Wakasugi T}}{Author{Hayashida N}}{Author{Matsubayashi T}}{Author{Zaita N}}{Author{Chunwongse J}}{Author{Obokata J}}{Author{Yamaguchi-Shinozaki K}}{Author{Ohto C}}{Author{Torazawa K}}{Author{Meng B.Y}}{Author{Sugita M}}{Author{Deno H}}{Author{Kamogashira T}}{Author{Yamada K}}{Author{Kusuda J}}{Author{Takaiwa F}}{Author{Kato A}}{Author{Tohdoh N}}{Author{Shimada H}}{Author{Sugiura M}}{Cite{EMBO J. 5;2043-2049(1986)}}}{Ref{num{5}}{pos{RNA EDITING OF INITIATOR CODON}}{Comment{SPECIES=N.TABACUM}}{DB{MEDLINE}}{MedlineID{97076156}}{Author{Chaudhuri S}}{Author{Maliga P}}{Cite{EMBO J. 15;5958-5964(1996)}}}{Ref{num{6}}{pos{SEQUENCE FROM N.A}}{Comment{SPECIES=H.VULGARE}}{Comment{STRAIN=CV. SABARLIS}}{DB{MEDLINE}}{MedlineID{89240046}}{Author{Chakhmakhcheva O.G}}{Author{Andreeva A.V}}{Author{Buryakova A.A}}{Author{Reverdatto S.V}}{Author{Efimov V.A}}{Cite{Nucleic Acids Res. 17;2858-2858(1989)}}}{Ref{num{7}}{pos{SEQUENCE FROM N.A}}{Comment{SPECIES=H.VULGARE}}{Comment{STRAIN=CV. SABARLIS}}{DB{MEDLINE}}{MedlineID{92207253}}{Author{Efimov V.A}}{Author{Andreeva A.V}}{Author{Reverdatto S.V}}{Author{Chakhmakhcheva O.G}}{Cite{Bioorg. Khim. 17;1369-1385(1991)}}}{Ref{num{8}}{pos{SEQUENCE FROM N.A}}{Comment{SPECIES=T.AESTIVUM}}{Comment{STRAIN=CV. SENTRY}}{Comment{TISSUE=LEAF}}{Author{Webber A.N}}{Author{Hird S.M}}{Author{Packman L.C}}{Author{Dyer T.A}}{Author{Gray J.C}}{Cite{Plant Mol. Biol. 12;141-151(1989)}}}{Ref{num{9}}{pos{RNA EDITING OF INITIATOR CODON}}{Comment{SPECIES=T.AESTIVUM}}{DB{MEDLINE}}{MedlineID{92191997}}{Author{Kudla J}}{Author{Igloi G.L}}{Author{Metzlaff M}}{Author{Hagemann R}}{Author{Koessel H}}{Cite{EMBO J. 11;1099-1103(1992)}}}{Ref{num{10}}{pos{SEQUENCE FROM N.A}}{Comment{SPECIES=S.CEREALE}}{DB{MEDLINE}}{MedlineID{89160331}}{Author{Zolotarev A.S}}{Author{Kolosov V.L}}{Cite{Nucleic Acids Res. 17;1760-1760(1989)}}}{Ref{num{11}}{pos{SEQUENCE FROM N.A}}{Comment{SPECIES=S.CEREALE}}{DB{MEDLINE}}{MedlineID{90073796}}{Author{Kolosov V.L}}{Author{Klezovich O.N}}{Author{Abdulaev N.G}}{Author{Zolotarev A.S}}{Cite{Bioorg. Khim. 15;1284-1286(1989)}}}{Ref{num{12}}{pos{SEQUENCE FROM N.A}}{Comment{SPECIES=MAIZE}}{Author{Haley J}}{Author{Bogorad L}}{Cite{Submitted (MAY-1989) to the EMBL/GenBank/DDBJ databases}}}{Ref{num{13}}{pos{SEQUENCE FROM N.A}}{Comment{SPECIES=MAIZE}}{DB{MEDLINE}}{MedlineID{95395841}}{Author{Maier R.M}}{Author{Neckermann K}}{Author{Igloi G.L}}{Author{Koessel H}}{Cite{J. Mol. Biol. 251;614-628(1995)}}}{Ref{num{14}}{pos{SEQUENCE FROM N.A}}{Comment{SPECIES=P.SATIVUM}}{DB{MEDLINE}}{MedlineID{89354671}}{Author{Willey D.L}}{Author{Gray J.C}}{Cite{Curr. Genet. 15;213-220(1989)}}}{Ref{num{15}}{pos{SEQUENCE FROM N.A., AND RNA EDITING OF INITIATOR CODON}}{Comment{SPECIES=S.OLERACEA}}{DB{MEDLINE}}{MedlineID{93360903}}{Author{Bock R}}{Author{Hagemann R}}{Author{Koessel H}}{Author{Kudla J}}{Cite{Mol. Gen. Genet. 240;238-244(1993)}}}{Ref{num{16}}{pos{SEQUENCE OF 1-12 FROM N.A}}{Comment{SPECIES=S.OLERACEA}}{Author{Hermann R.G}}{Author{Alt J}}{Author{Schiller B}}{Author{Widger W.R}}{Author{Cramer W.A}}{Cite{FEBS Lett. 176;239-244(1984)}}}{Ref{num{17}}{pos{SEQUENCE FROM N.A., AND RNA EDITING OF INITIATOR CODON}}{Comment{SPECIES=C.ANNUUM}}{Comment{STRAIN=CV. LAMUYO}}{Comment{TISSUE=LEAF, AND FRUIT}}{DB{MEDLINE}}{MedlineID{93099270}}{Author{Kuntz M}}{Author{Camara B}}{Author{Weil J.-H}}{Author{Schantz R}}{Cite{Plant Mol. Biol. 20;1185-1188(1992)}}}{Ref{num{18}}{pos{SEQUENCE FROM N.A}}{Comment{SPECIES=M.CRYSTALLINUM}}{DB{MEDLINE}}{MedlineID{94345017}}{Author{Forsthoefel N.R}}{Author{Cushman J.C}}{Cite{Plant Physiol. 105;761-762(1994)}}}{Ref{num{19}}{pos{SEQUENCE FROM N.A}}{Comment{SPECIES=B.VULGARIS}}{Comment{STRAIN=CV. TK81-O}}{Comment{TISSUE=LEAF}}{DB{MEDLINE}}{MedlineID{95254673}}{Author{Kubo T}}{Author{Yanai Y}}{Author{Kinoshita T}}{Author{Mikami T}}{Cite{Curr. Genet. 27;285-289(1995)}}}{Ref{num{20}}{pos{SEQUENCE FROM N.A}}{Comment{SPECIES=P.DELTOIDES}}{Comment{STRAIN=CV. STONEVILLE D121}}{Comment{TISSUE=LEAF}}{Author{Naithani S}}{Cite{Submitted (FEB-1997) to the EMBL/GenBank/DDBJ databases}}}{Ref{num{21}}{pos{SEQUENCE OF 1-15 FROM N.A}}{Comment{SPECIES=T.AESTIVUM, AND S.OLERACEA}}{DB{MEDLINE}}{MedlineID{89121082}}{Author{Ikeuchi M}}{Author{Takio K}}{Author{Inoue Y}}{Cite{FEBS Lett. 242;263-269(1989)}}}{Ref{num{22}}{pos{SEQUENCE OF 1-3, AND MASS-SPECTROMETRY}}{Comment{SPECIES=S.OLERACEA}}{DB{MEDLINE}}{MedlineID{98298118}}{Author{Zheleva D}}{Author{Sharma J}}{Author{Panico M}}{Author{Morris H.R}}{Author{Barber J}}{Cite{J. Biol. Chem. 273;16122-16127(1998)}}}{EMBL{prim_id{X15901}}{sec_id{CAA33963}}}{EMBL{prim_id{Z00044}}{sec_id{-}}{status{NOT_ANNOTATED_CDS}}}{EMBL{prim_id{X15767}}{sec_id{CAA33774}}}{EMBL{prim_id{X14108}}{sec_id{-}}{status{NOT_ANNOTATED_CDS}}}{EMBL{prim_id{X15225}}{sec_id{CAA33296}}}{EMBL{prim_id{J04502}}{sec_id{AAA84476}}}{EMBL{prim_id{X86563}}{sec_id{CAA60300}}}{EMBL{prim_id{X13326}}{sec_id{CAA31700}}}{EMBL{prim_id{M35673}}{sec_id{-}}{status{NOT_ANNOTATED_CDS}}}{EMBL{prim_id{X70699}}{sec_id{CAA50030}}}{EMBL{prim_id{X65570}}{sec_id{CAA46540}}}{EMBL{prim_id{U04314}}{sec_id{AAA21859}}}{EMBL{prim_id{D38019}}{sec_id{BAA07220}}}{EMBL{prim_id{X89651}}{sec_id{CAA61800}}}{PIR{prim_id{JQ0241}}{sec_id{F2RZL}}}{PIR{prim_id{JG0010}}{sec_id{F2WTL}}}{PIR{prim_id{S04064}}{sec_id{S04064}}}{PIR{prim_id{S05685}}{sec_id{S05685}}}{PIR{prim_id{S03193}}{sec_id{S03193}}}{PIR{prim_id{JN0358}}{sec_id{JN0358}}}{PIR{prim_id{S28056}}{sec_id{S28056}}}{PIR{prim_id{C48310}}{sec_id{C48310}}}{MAIZEDB{prim_id{69558}}{sec_id{-}}}{Keyword{Photosynthesis}}{Keyword{Photosystem II}}{Keyword{Chloroplast}}{Keyword{RNA editing}}{Features{INIT_MET{from{0}}{to{0}}}}}", 
  			726.0);
  }
    
  @Test
  public void distanceTest() {
    symmetricTest("{a{b}{c{e}{f}}{d}}", "{a{b}{c{e}{f}}{d}}", 0.0);
    symmetricTest("{a{b}{c{e}{f}}{d}}", "{a{b}{c{e}}{d}}", 1.0);
    symmetricTest("{a{b}{c{e}{f}}{d}}", "{a{b}{c}{d}}", 2.0);
    symmetricTest("{a{x}{c{e}{f}}{d}}", "{a{b}{c}{d}}", 3.0);
    symmetricTest("{a{b}{c{e}{f}}{d}}", "{a{b}{c}}", 3.0);

    symmetricTest("{a{b{i}{j{u}}}{c{d}{e{q{n}{m}}}}{f{w}}}", "{a}", 12.0);

    symmetricTest("{a{b{i}{j{u}}}{c{d}{e{q{n}{m}}}}{f{w}}}", "{x}", 13.0);
    symmetricTest("{a{b{i}{j{u}}}{c{d}{e}}{f{w}}}", "{x}", 10.0);
    symmetricTest("{a{b{i}{j}}{c{d}{e}}{f{w}}}", "{x}", 9.0);
    symmetricTest("{a{b{i}}{c{d}{e}}{f{w}}}", "{x}", 8.0);
    symmetricTest("{a{b}{c{d}{e}}{f{w}}}", "{x}", 7.0);
    symmetricTest("{a{b}{c{d}{e}}{f}}", "{x}", 6.0);

    symmetricTest("{a{m}{r}{d}{e{z}{i}{l}{t{o}{k}{g}{h}}}}", "{x}", 13.0);
    symmetricTest("{x}", "{a{m}{r}{d}{e{z}{i}{l}{t{o}{k}{g}{h}}}}", 13.0);
    symmetricTest("{a{r}{d}{e{i}{l}{t{k}{g}{h}}}}", "{x}", 10.0);
    symmetricTest("{x}", "{a{r}{d}{e{i}{l}{t{k}{g}{h}}}}", 10.0);
    symmetricTest("{a{r}{d}{e{s}{t}}}", "{x}", 6.0);
    symmetricTest("{x}", "{a{d}{e{l}{t{g}{h}}}}", 7.0);
    symmetricTest("{a{d}{e{l}{t{g}{h}}}}", "{x}", 7.0);
    symmetricTest("{a{d}{e}{f}{l}{t}}", "{f}", 5.0);
    symmetricTest("{a{d}{e}{f}{l}{t}}", "{a}", 5.0);
    symmetricTest("{a{d}{e}{f}{l}{t}}", "{x}", 6.0);
    symmetricTest("{a{d}{e}{f}}", "{x}", 4.0);
    symmetricTest("{x}", "{a{d}{e}{f}}", 4.0);

    symmetricTest("{a{b{c}{d{e{f}{g}}{h}}}{i}}", "{e{f}{g}}", 6.0);
  	symmetricTest("{a{b}{c{d}{e{f}{g{h}{i}}}}}", "{g{h}{i}}", 6.0);
  	symmetricTest("{a{b{d{f{h}{i}}{g}}{e}}{c}}", "{f{h}{i}}", 6.0);
  	symmetricTest("{a{b}{c{d{f}{g{h}{i}}}{e}}}", "{g{h}{i}}", 6.0);
    symmetricTest("{b{d}{e}}", "{g{h}{i}}", 3.0);
    symmetricTest("14:{a{b{d}{e}}{c}}", "14:{f{g{h}{i}}{k}}:", 5.0); 
    symmetricTest("18:{f{d{a}{c{b}}}{e}}", "18:{f{c{d{a}{b}}{e}}}", 2.0);
    symmetricTest("17:{f{d{a}{c{b}}}{e}}", "17:{f{c{d{a}{b}}}{x}}", 3.0);
    symmetricTest("16:{f{d{a}{c{b}}}{e}}", "16:{f{c{d{a}{b}}}{e}}", 2.0);
    symmetricTest("12:{a{a{a}{a}}}", "12:{a{a{a}}}", 1.0);
    symmetricTest("11:{a{b}{c{d}{e}}}", "11:{a{b{c}}{d}{e}}", 2.0);
    symmetricTest("{a{b{c}{d{e}{f}}}{x}}}", "{b{c}{d{e}{f}}}", 2.0);

    symmetricTest("{a{b{c}{d{e}{f}}}}", "{b{c}{d{e}{f}}}", 1.0);
    symmetricTest("{a{b{c}{d{e}{f}}}}", "{a{c}{e}{f}}", 2.0);
    symmetricTest("{a{b{c}{d}}}", "{a{c}{d}}", 1.0);
    symmetricTest("{a{b{c}}{d}}", "{b{c}{a{d}}}", 3.0);
    symmetricTest("{a{b}{c}}", "{b{c}}", 2.0);
    symmetricTest("{a{b}{c}}", "{b}", 2.0);
    symmetricTest("{a{b}}", "{b}", 1.0);
    symmetricTest("{a{b}{c}}", "{x}", 3.0);
    symmetricTest("{a{b}{c}}", "{a}", 2.0);
    symmetricTest("{a{b}}", "{x{z}}", 2.0);
    symmetricTest("{a{b}}", "{a{b}}", 0.0);
    symmetricTest("{a{b}}", "{x}", 2.0);
    symmetricTest("{a{b}}", "{a}", 1.0);
    symmetricTest("{a}", "{x}", 1.0);
    symmetricTest("{a}", "{a}", 0.0);

    symmetricTest("1:{a}", "1:{a}", 0.0);
		symmetricTest("2:{a}", "2:{b}", 1.0);
		symmetricTest("3:{a{b}}", "3:{b}", 1.0);
		symmetricTest("4:{a{b}{c}}", "4:{b}", 2.0);
		symmetricTest("5:{a{b}{c}}", "5:{b{c}}", 2.0);
		symmetricTest("6:{a{b{c}}{d}}", "6:{b{c}{a{d}}}", 3.0);
		symmetricTest("7:{a{b{c}{d}}}", "7:{a{c}{d}}", 1.0);
		symmetricTest("8:{a{b{c}{d{e}{f}}}}", "8:{a{c}{e}{f}}", 2.0);		
		symmetricTest("9:{a{b{c}{d{e}{f}}}}", "9:{b{c}{d{e}{f}}}", 1.0);
		symmetricTest("10:{a{b{c}{d{e}{f}}}{x}}", "10:{b{c}{d{e}{f}}}", 2.0);		
		symmetricTest("11:{a{b}{c{d}{e}}}", "11:{a{b{c}}{d}{e}}", 2.0);
		symmetricTest("12:{a{a{a}{a}}}", "12:{a{a{a}}}", 1.0);
		symmetricTest("13:{a{a{a}{a{a}{a}}}{a{a}{a{a}}{a}}}", "13:{a{a{a}{a}{a}}{a{a{a}{a}{a}}}}", 3.0);
		symmetricTest("14:{a{b{d}{e}}{c}}", "14:{f{g{h}{i}}{k}}:", 5.0);						
		symmetricTest("15:{a{b{c}{d{e}{f}}}}", "15:{b{c}{e}{f}}", 2.0);		
		symmetricTest("16:{f{d{a}{c{b}}}{e}}", "16:{f{c{d{a}{b}}}{e}}", 2.0);
		symmetricTest("17:{f{d{a}{c{b}}}{e}}", "17:{f{c{d{a}{b}}}{x}}", 3.0);
		symmetricTest("18:{f{d{a}{c{b}}}{e}}", "18:{f{c{d{a}{b}}{e}}}", 2.0);
  }
	
}
