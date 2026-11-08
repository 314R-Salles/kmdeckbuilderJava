# Facettes sur la recherche de decks

## Question

Peut-on produire des facettes (Dieu, propriétaire, carte contenue, tag) sur la stack
actuelle (Java / MySQL), sans moteur de recherche dédié, en ne comptant que la dernière
version de chaque deck ?

## Conclusion

Oui, en SQL pur. La table `km_last_version` joue déjà le rôle d'index sur données stables :
elle n'est écrite qu'au moment du save d'un deck (`DeckService`), ce qui donne une source de
vérité unique pour « quelle version compte ».

Une facette = un `GROUP BY ... COUNT(*)` sur la jointure `km_deck ⋈ km_last_version`,
filtrée par les critères de recherche courants :

```sql
SELECT d.god, COUNT(*) FROM km_deck d
JOIN km_last_version lv ON lv.deckId = d.deckId AND lv.version = d.version
WHERE <filtres courants sauf god>
GROUP BY d.god;
```

## Points d'attention

**Sémantique (drill-down).** Pour le comportement de facette classique, chaque dimension doit
être comptée en excluant son propre filtre. Cela impose une requête par dimension avec un
`WHERE` différent, pas une requête unique.

**Coût.** Dieu / propriétaire / tag sont triviaux. La facette **cartes** est la seule vraiment
chère : `km_association` explose à ~40 lignes par deck et la cardinalité de sortie est de
plusieurs centaines de cartes. Elle nécessite un index sur `km_association(cardId)` en plus de
la PK, et il faudra probablement renvoyer un top N plutôt que la distribution complète.

## Stratégie retenue

1. Commencer en `GROUP BY` à la volée : quelques ms à la volumétrie actuelle, aucun état à
   maintenir.
2. Si la facette cartes devient le point chaud, matérialiser une table de facettes rafraîchie
   à l'écriture, au même endroit que `LastVersionEntity` dans `DeckService`, pour conserver
   l'invariant « une seule source de vérité pour la dernière version ».

Pas de besoin d'Elasticsearch à ce stade.
