code

	Dans ce dossier, vous pouvez trouver mon code, où Main.java est la classe principale, pour exécuter cette application, voir la méthode main.
	D'abord javac Main.java pour compiler.
	Puis java Main pour l'exécuter

dataSet
Il s'agit de l'ensemble de données contenant notre mdvrp, voir  DATASET-DESCRPTION.md  pour plus de détails.

papers
Il s'agit de la somme totale des articles auxquels nous nous référons, l'article auquel nous nous comparons étant IJOR160304_Geetha.pdf.


resultGraphics
Il contient nos meilleurs résultats, et nous ne les sauvegardons que s'ils dépassent les meilleurs résultats connus de l'article.

results
Ce dossier contient tous les chemins optimaux trouvés par notre application, dans le format suivant
La première ligne contient le coût total du chemin, par exemple 1318.956332
Les autres lignes sont les chemins légaux à partir d'un dépôt.
Par exemple 84 11 25 36 14 ...
