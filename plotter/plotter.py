import matplotlib.pyplot as plt
import numpy

LEFT_SIDE = 0.12


class Particle:

    def __init__(self, particle_id, coordinate_x, coordinate_y):
        self.particle_id = particle_id
        self.coordinate_x = coordinate_x
        self.coordinate_y = coordinate_y


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
                event.particles.append(Particle(particle, coordinate_x, coordinate_y))
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
            event.particles.append(Particle(particle, coordinate_x, coordinate_y))
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
                event.particles.append(Particle(particle, coordinate_x, coordinate_y))
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


def get_finish_time(file_name):
    event_array, event_counter = get_all_events(file_name)
    return (event_array)[-1].time


def get_finish_and_error_time(time_array):
    return numpy.average(time_array), numpy.std(time_array)


def plot_fp_vs_time(file_name, label, color):
    event_array, event_counter = get_all_events(file_name)
    fp_array = get_fp_array(event_array)
    time_array = map(lambda x: x.time, event_array)
    plt.plot(time_array, fp_array, label=label, color=color)
    plt.plot(time_array, map(lambda x: 1 - x, fp_array), color=color)


def plot_all_fp_time():
    prop_cycle = plt.rcParams['axes.prop_cycle']
    colors = prop_cycle.by_key()['color']
    plot_fp_vs_time('../resources/time_vs_slot/m=0.01/outputFile1', 'm=0.01', colors[0])
    plot_fp_vs_time('../resources/time_vs_slot/m=0.02/outputFile1', 'm=0.02', colors[1])
    plot_fp_vs_time('../resources/time_vs_slot/m=0.04/outputFile1', 'm=0.04', colors[2])
    plt.xlabel("Time(s)")
    plt.ylabel("fp")
    plt.legend()
    plt.grid()
    plt.show()


def plot_time_vs_N(folderName, N):
    time_array = []
    for i in range(10):
        time_array.append(get_finish_time(folderName + '/outputFile' + str(i)))
    average, std = get_finish_and_error_time(time_array)
    return average, std


def plot_all_time_vs_N():
    N = [0.01, 0.02, 0.04]
    average = []
    std = []
    aux1, aux2 = plot_time_vs_N('../resources/time_vs_slot/m=0.01', 50)
    average.append(aux1)
    std.append(aux2)
    aux1, aux2 = plot_time_vs_N('../resources/time_vs_slot/m=0.02', 100)
    average.append(aux1)
    std.append(aux2)
    aux1, aux2 = plot_time_vs_N('../resources/time_vs_slot/m=0.04', 150)
    average.append(aux1)
    std.append(aux2)
    plt.errorbar(N, average, fmt='o-', yerr=std)
    plt.xticks([0.00, 0.01, 0.02, 0.03, 0.04, 0.05])
    plt.xlabel("Slot(m)")
    plt.ylabel("Time(s)")
    plt.grid()
    plt.show()


def plot_p_vs_temp():
    v_1 = [0.01, [0.124647827, 0.124641916, 0.123746955, 0.123923138, 0.123916743, 0.1248471, 0.124177219, 0.124342504,
                  0.124173333, 0.124358179],
           [3.87E-05, 3.76E-05, 3.88E-05, 3.94E-05, 4.05E-05, 4.22E-05, 3.87E-05, 4.08E-05, 4.06E-05, 3.89E-05]]
    v_2 = [0.03, [1.114916646,1.118361682,1.114803643,1.119713475,1.117654131, 1.116001455, 1.116563646, 1.120206277, 1.114700817, 1.1161263],
           [3.48E-04,3.44E-04,3.42E-04,3.69E-04,3.45E-04,3.44E-04,3.26E-04,3.37E-04,3.56E-04,3.72E-04]]
    v_3 = [0.05, [3.105657055,
                  3.099397987,
                  3.096586305,
                  3.095683035,
                  3.098004728,
                  3.097993731,
                  3.09788308,
                  3.096439321,
                  3.104816864,
                  3.102909109],[0.001063027,9.84E-04,9.53E-04,9.43E-04,9.81E-04,9.49E-04,0.001022281,0.001023051,0.001027535,9.41E-04]]
    pressure = [numpy.mean(v_1[1]), numpy.mean(v_2[1]), numpy.mean(v_3[1])]
    pressure_error = [numpy.std(v_1[1]), numpy.std(v_2[1]), numpy.std(v_3[1])]
    temp = [numpy.mean(v_1[2]), numpy.mean(v_2[2]), numpy.mean(v_3[2])]
    temp_error = [numpy.std(v_1[2]), numpy.std(v_2[2]), numpy.std(v_3[2])]
    plt.plot(temp, pressure, '-o')
    plt.ylabel('Pressure')
    plt.xlabel('Temperature')
    aux = ['v=0.01','v=0.03','v=0.05']
    for i in range(len(temp)):
        if(i == 2):
            plt.annotate(aux[i],(temp[i] - 0.00015,pressure[i]))
        else:
            plt.annotate(aux[i],(temp[i] + 0.00005,pressure[i]))
    plt.grid()
    plt.show()


if __name__ == '__main__':
    plot_p_vs_temp()
