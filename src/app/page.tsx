import { Button } from "~/components/ui/button";
import { PlayIcon } from "~/app/_components/icons";
import Image from "next/image";

import { api } from "~/trpc/server";
import VideoCard, {SkeletonVideoCard} from "~/app/_components/videocard";
import { lazy, Suspense } from "react";
import entry from "next/dist/server/typescript/rules/entry";
import {Entry} from "../../prisma/generated/zod";
import {Skeleton} from "~/components/ui/skeleton";

export default async function Home() {
  const entries: Entry[] = await api.entries.getAll({});
  return (
    <div className="grid grid-cols-2 gap-4 p-6 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
      {/*<div className="group relative overflow-hidden rounded-lg">*/}
      {/*  <Image*/}
      {/*    alt="Video Thumbnail"*/}
      {/*    className="h-[200px] w-full object-cover transition-all group-hover:scale-105"*/}
      {/*    height={200}*/}
      {/*    src="https://placehold.co/300x200/070707/f0f0f0/svg?text=Placeholder"*/}
      {/*    style={{ aspectRatio: "300/200", objectFit: "cover" }}*/}
      {/*    width={300}*/}
      {/*  />*/}
      {/*  <div className="absolute inset-0 flex items-center justify-center bg-black/50 opacity-0 transition-opacity group-hover:opacity-100">*/}
      {/*    <Button className="text-white" size="icon" variant="ghost">*/}
      {/*      <PlayIcon className="h-6 w-6" />*/}
      {/*    </Button>*/}
      {/*  </div>*/}
      {/*  <div className="p-2">*/}
      {/*    <h3 className="line-clamp-2 text-sm font-medium text-gray-50"></h3>*/}
      {/*  </div>*/}
      {/*</div>*/}
      <Suspense fallback={null}>

        {entries.map(entry => (
          <Suspense key={entry.id} fallback={<SkeletonVideoCard/>}><VideoCard key={entry.id} entry={entry} /></Suspense>
        ))}
      </Suspense>
    </div>
  );
}
