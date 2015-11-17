# Greatest connected Graph with only UNION
import random


def union(node_a, node_b):
    if node_a.representative is not None and \
            node_b.representative is not None:
        raise Exception("at least one must be []")
    if node_a.representative is None and node_b.representative is None:
        node_b.representative = node_a
        node_a.size += 1
        return node_a
    elif node_a.representative is None:
        node_a.representative = node_b.representative
        node_b.representative.size += 1
        return node_b.representative
    else:
        node_b.representative = node_a.representative
        node_a.representative += 1
        return node_a.representative


def gen_id():
    num = 0
    while True:
        yield num
        num += 1

id_generator = gen_id()


class Node:
    """
    Node
    """
    seen = False
    inQueue = False
    representative = None
    black = False
    id = -1
    size = 1

    def __init__(self, black):
        self.black = black
        self.id = next(id_generator)

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        if self.black:
            return "B"
        return "_"

n = 10
grid = [None] * n
for x in range(0, n):
    grid[x] = [None] * n
    for y in range(0, n):
        if random.random() > .5:
            grid[x][y] = Node(True)
        else:
            grid[x][y] = Node(False)


def print_grid(G, ids=False, reps=False):
    n = len(G)
    for x in range(0, n):
        line = ""
        for y in range(0, n):
            id = ""
            repsstr = ""
            if (ids):
                id = "|" + str(G[x][y].id).zfill(2)
            if (reps):
                rep = G[x][y].representative
                if rep is None:
                    rep = G[x][y]
                if (G[x][y].black):
                    repsstr = "|" + str(rep.id).zfill(2)
                else:
                    repsstr = "|__"
            line += "[" + str(G[x][y]) + id + repsstr + "]"
        print(line)

print_grid(grid, ids=False, reps=True)


def nodes(x, y, n):
    nds = []
    if x > 0:
        nds.append((x-1, y))
    if x < (n-1):
        nds.append((x+1, y))
    if y > 0:
        nds.append((x, y-1))
    if y < (n-1):
        nds.append((x, y+1))
    return nds


bfs_times = 0
def bfs_black(grid, x, y, Q):
    """

    :param grid:
    :param x:
    :param y:
    :return: (size of island, repr. of island)
    """
    global bfs_times
    bfs_times += 1
    n = len(grid)
    island_rep = grid[x][y].representative
    if island_rep is None:
        island_rep = grid[x][y]
    if grid[x][y].black:
        grid[x][y].seen = True
        for (nx, ny) in nodes(x, y, n):
            if not grid[nx][ny].seen:
                if grid[nx][ny].black:
                    grid[nx][ny].seen = True
                    island_rep = union(island_rep, grid[nx][ny])
                    bfs_black(grid, nx, ny, Q)
                elif not grid[nx][ny].inQueue:
                    grid[nx][ny].inQueue = True
                    Q.append((nx, ny))
        return island_rep.size, island_rep
    else:
        grid[x][y].seen = True
        for (nx, ny) in nodes(x, y, n):
            if not grid[nx][ny].seen and not grid[nx][ny].inQueue:
                grid[nx][ny].inQueue = True
                Q.append((nx, ny))
        return island_rep.size, island_rep



def find_greatest_connected_graph(grid):
    global bfs_times
    Q = []
    max_gcg = -1
    max_gcg_rep = None

    grid[0][0].inQueue = True
    Q.append((0, 0))
    total_Q = 0

    while(Q):
        total_Q += 1
        (x, y) = Q.pop()
        if not grid[x][y].seen:
            gcg, gcg_rep = bfs_black(grid, x, y, Q)
            if gcg > max_gcg:
                max_gcg = gcg
                max_gcg_rep = gcg_rep

    print("TOTAL Q:" + str(total_Q) + " bfs calls:" + str(bfs_times))
    if max_gcg_rep is None:
        return 0, None
    return max_gcg, max_gcg_rep.id

SIZE, REP = find_greatest_connected_graph(grid)
print("Size:" + str(SIZE) + " REP:" + str(REP))
print_grid(grid, ids=False, reps=True)

