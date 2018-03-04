import argparse
import io

from google.cloud import vision
from google.cloud.vision import types


def annotate(path):
    """Returns web annotations given the path to an image."""
    client = vision.ImageAnnotatorClient()

    if path.startswith('http') or path.startswith('gs:'):
        image = types.Image()
        image.source.image_uri = path

    else:
        with io.open(path, 'rb') as image_file:
            content = image_file.read()

        image = types.Image(content=content)

    web_detection = client.web_detection(image=image).web_detection

    return web_detection


def report(annotations):
    #Lista de marcas (las sacamos de mysql)
    list=["Rolls", "Peugeot", "Citroën"]
    #Flag para cuando el coche se encuentra
    a=0

    #Recorremos la lista de todas las coincidencias encontradas de la imagen
    for entity in annotations.web_entities:
        #Recorremos la lista de marcas
        for s in list:
            #Si encuentra la marca en la coincidencia, levantamos la flag de coche encontrado y nos salimos del bucle del todo
            #Quiere decir que encontro la primera coincidencia del coche con la puntuacion mas alta
            #Es necesario todo esto debido a un bug que a veces no ordena adecuadamente las coincidencias y no siempre la primera es la buena
            if (s in entity.description):
                print(entity.description)
                a=1
            if (a==1):
                break
        if (a==1):
            break


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter)
    path_help = str('The image to detect, can be web URI, '
                    'Google Cloud Storage, or path to local file.')
    parser.add_argument('image_url', help=path_help)
    args = parser.parse_args()

    report(annotate(args.image_url))