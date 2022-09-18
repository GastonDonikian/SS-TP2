import matplotlib.pyplot as plt

LEFT_SIDE = 0.12


class Particle:

    def __init__(self, particle_id, coordinate_x, coordinate_y, speed_x, speed_y):
        self.particle_id = particle_id
        self.coordinate_x = coordinate_x
        self.coordinate_y = coordinate_y
        self.speed_x = speed_x
        self.speed_y = speed_y


class Event:

    def __init__(self, time, event):
        self.event = event
        self.time = time
        self.particles = []


def get_every_N_events(N, file_name):
    output = open(file_name, 'r')
    particles = int(output.readline())
    event_counter = 0
    aux = output.readline()
    event_array = []
    while aux.startswith('t'):
        aux = aux.split(" ")
        if int(aux[3]) % N == 0:
            event_counter += 1
            event = Event(float(aux[1]), int(aux[3].strip()))
            for particle in range(particles):
                particle_info = output.readline().split(" ")
                particle_info[-1] = particle_info[-1].strip()
                coordinate_x = float(particle_info[0])
                coordinate_y = float(particle_info[1])
                speed_x = float(particle_info[2])
                speed_y = float(particle_info[3])
                event.particles.append(Particle(particle, coordinate_x, coordinate_y, speed_x, speed_y))
            event_array.append(event)
        else:
            for i in range(particles):
                output.readline()
        aux = output.readline()
        aux = output.readline()
    return event_array, event_counter


def get_all_events(file_name):
    output = open(file_name, 'r')
    particles = int(output.readline())
    event_counter = 0
    aux = output.readline()
    event_array = []
    while aux.startswith('t'):
        event_counter += 1
        aux = aux.split(" ")
        event = Event(float(aux[1]), int(aux[3].strip()))
        for particle in range(particles):
            particle_info = output.readline().split(" ")
            particle_info[-1] = particle_info[-1].strip()
            coordinate_x = float(particle_info[0])
            coordinate_y = float(particle_info[1])
            speed_x = float(particle_info[2])
            speed_y = float(particle_info[3])
            event.particles.append(Particle(particle, coordinate_x, coordinate_y, speed_x, speed_y))
        event_array.append(event)
        aux = output.readline()
        aux = output.readline()
    return event_array, event_counter


def get_events_by_time(interval, file_name):
    output = open(file_name, 'r')
    particles = int(output.readline())
    event_counter = 0
    aux = output.readline()
    event_array = []
    while aux.startswith('t'):
        aux = aux.split(" ")
        if float(aux[1]) > interval * event_counter:
            event_counter += 1
            event = Event(float(aux[1]), int(aux[3].strip()))
            for particle in range(particles):
                particle_info = output.readline().split(" ")
                particle_info[-1] = particle_info[-1].strip()
                coordinate_x = float(particle_info[0])
                coordinate_y = float(particle_info[1])
                speed_x = float(particle_info[2])
                speed_y = float(particle_info[3])
                event.particles.append(Particle(particle, coordinate_x, coordinate_y, speed_x, speed_y))
            event_array.append(event)
        else:
            for i in range(particles):
                output.readline()
        aux = output.readline()
        aux = output.readline()
    return event_array, event_counter


def get_fp_by_event(event):
    fp = 0
    for particle in event.particles:
        if particle.coordinate_x < LEFT_SIDE:
            fp += 1
    return float(fp) / len(event.particles)


def get_fp_array(events):
    fp_array = []
    for event in events:
        fp_array.append(get_fp_by_event(event))
    return fp_array


if __name__ == '__main__':
    event_array, event_counter = get_every_N_events(20,'../resources/outputFile')
    time_array = []
    for i in range(event_counter):
        time_array.append(i*20)
    plt.plot(time_array,  get_fp_array(event_array))
    plt.plot(time_array,map(lambda x : 1 - x,  get_fp_array(event_array)))
    plt.title("Fp vs Events")
    plt.xlabel("Events")
    plt.ylabel("fp")
    plt.show()
