#prolog#
node(node_id, line_id, x, y)
line(id, limit, toll)
client(x, y, x_dest, y_dest, time, people, language)
taxi(x, y, id, capacity, language, rating, long_distance)
child(node1, node2)
distance(node1, node2, time, dist)
can_ride(client, taxi)

##long distance limit##
50

##traffic coefficients##
high: 0.3
medium: 0.6
low: 0.9
