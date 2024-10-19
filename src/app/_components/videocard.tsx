import {type Entry, Provider, User} from "../../../prisma/generated/zod";
import Image from "next/image";
import {Button} from "~/components/ui/button";
import {PlayIcon} from "~/app/_components/icons";
import {Skeleton} from "~/components/ui/skeleton";
import {Avatar, AvatarImage, AvatarFallback} from "~/components/ui/avatar";
import {api} from "~/trpc/server";
import {Badge, BadgeAlertIcon, BadgeCheckIcon, BadgeIcon, InfoIcon} from "lucide-react";

interface VideoCardProps {
  entry: Entry;
}


export default async function VideoCard(props: VideoCardProps) {
  const availabilities: { provider: Provider; url: string }[] =
    await api.providers.getVideoProviders({
      id: props.entry.id,
      freeOnly: false,
    });
  const entryOpener: User = await api.user.getById({
    id: props.entry.createdById,
  });
  return (
    <div className="group relative w-[300px] overflow-hidden rounded-lg bg-gray-900">
      <div className={"h-[200px] overflow-hidden"}>
        <Image
          alt="Video Thumbnail"
          className="w-full object-cover transition-all group-hover:scale-105"
          height={200}
          src={props.entry.thumbnail}
          style={{ aspectRatio: "300/200", objectFit: "cover" }}
          width={300}
        />
        <div className="absolute inset-0 flex h-[200px] items-center justify-center bg-black/50 opacity-0 transition-opacity group-hover:opacity-100">
          <Button className="text-white" size="icon" variant="ghost">
            <InfoIcon className="h-6 w-6" />
          </Button>
        </div>
      </div>
      <div className={"absolute left-3 top-3 h-5 w-5 text-white"}>
        {!props.entry.watched &&
          props.entry.createdAt.getTime() >
            Date.now() - 1000 * 60 * 60 * 24 && (
            <BadgeAlertIcon className={"h-full w-full"} />
          )}
        {!props.entry.watched &&
          props.entry.createdAt.getTime() <
            Date.now() - 1000 * 60 * 60 * 24 && (
            <BadgeIcon className={"h-full w-full"} />
          )}
        {props.entry.watched && <BadgeCheckIcon/>}
      </div>
      <div className="m-2">
        <h3 className="line-clamp-2 text-sm font-medium text-gray-50">
          {props.entry.title}
        </h3>
      </div>
      <div className="m-1 flex">
        <div className={"flex w-[150px]"}>
          <Avatar
            key={entryOpener.id}
            className={"-ml-4 h-8 w-8 first:ml-0 last:mr-2"}
          >
            {entryOpener.image &&
              !(
                entryOpener.image ==
                "https://cdn.discordapp.com/embed/avatars/0.png"
              ) && (
                <AvatarImage
                  src={entryOpener.image}
                  alt={entryOpener.name}
                  title={entryOpener.name}
                />
              )}
            <AvatarFallback className={"bg-gray-800 text-gray-50"}>
              {entryOpener.name.charAt(0).toUpperCase()}
            </AvatarFallback>
          </Avatar>
        </div>
        <div className={"flex w-[150px] justify-end"}>
          {availabilities.map((availability) => (
            <Avatar key={availability.provider.name} className={"mr-1 h-8 w-8"}>
              <AvatarImage
                src={availability.provider.icon}
                alt={availability.provider.name}
                title={availability.provider.name}
              />
              <AvatarFallback className={"bg-gray-800 text-gray-50"}>
                {availability.provider.name
                  .split(" ")
                  .map((value) => value.charAt(0).toUpperCase())
                  .toString()}
              </AvatarFallback>
            </Avatar>
          ))}
        </div>
      </div>
    </div>
  );
}

export async function SkeletonVideoCard() {
  return (
    <div className="group relative overflow-hidden rounded-lg">
      <Skeleton className="h-[200px] w-[300px]" />
      <div className="p-2">
        <Skeleton className="h-5 w-[150px]" />
      </div>
      <div className="p-1">
        <Skeleton className="float-right h-8 w-[150px]" />
        <Skeleton className="float-left h-8 w-[150px]" />
      </div>
    </div>
  );
}