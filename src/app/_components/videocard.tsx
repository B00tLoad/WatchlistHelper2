import {type Entry} from "../../../prisma/generated/zod";
import Image from "next/image";
import {Button} from "~/components/ui/button";
import {PlayIcon} from "~/app/_components/icons";
import {Skeleton} from "~/components/ui/skeleton";

interface VideoCardProps {
  entry: Entry;
}

export default async function VideoCard(props: VideoCardProps) {
  return (
    <div className="group relative overflow-hidden rounded-lg">
      <Image
        alt="Video Thumbnail"
        className="h-[200px] w-full object-cover transition-all group-hover:scale-105"
        height={200}
        src={props.entry.thumbnail}
        style={{ aspectRatio: "300/200", objectFit: "cover" }}
        width={300}
      />
      <div className="absolute inset-0 flex items-center justify-center bg-black/50 opacity-0 transition-opacity group-hover:opacity-100">
        <Button className="text-white" size="icon" variant="ghost">
          <PlayIcon className="h-6 w-6" />
        </Button>
      </div>
      <div className="p-2">
        <h3 className="line-clamp-2 text-sm font-medium text-gray-50">
          {props.entry.title}
        </h3>
      </div>
    </div>
  );
}

export async function SkeletonVideoCard() {
  return (
    <div className="flex flex-col space-y-3">
      <Skeleton className="h-[200px] w-[300px]"/>
      <div className="space-y-2">
        <Skeleton className="p-2 h-5 w-[150px]"/>
      </div>
    </div>
  )
}